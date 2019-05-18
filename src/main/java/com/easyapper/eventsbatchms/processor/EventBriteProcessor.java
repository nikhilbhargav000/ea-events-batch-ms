package com.easyapper.eventsbatchms.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.model.eventbrite.EventBriteAddressDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteVenueDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteDto.BriteDateTime;
import com.easyapper.eventsbatchms.model.postevent.EventBookingDto;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.model.postevent.LocationDto;
import com.easyapper.eventsbatchms.provider.EventBriteProvider;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.utilities.EABatchUtil;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventBriteProcessor implements ItemProcessor<EventBriteDto, List<EventDto>> {

	@Autowired
	EALogger logger;
	@Autowired
	EABatchUtil util;
	@Autowired
	EventProcessorHelper processorHelper;
	@Autowired
	EventBriteProvider briteProvider;
	@Autowired
	EABatchConstants contants;
	
	private final String REGEX_PRICE_CURRENCY = ".*priceCurrency\":\"(\\w+)\".*";
	private final String REGEX_LOW_PRICE = ".*\"lowPrice\":([0-9]+\\.[0-9]+).*";
	private final String REGEX_HIGH_PRICE = ".*\"highPrice\":([0-9]+\\.[0-9]+).*";
	
	@Override
	public List<EventDto> process(EventBriteDto readEvent) throws Exception {
		
		logger.info("In EventBriteProcessor : process | eventId : " + readEvent.getId());
		
		EventBriteVenueDto briteVenue = Optional.ofNullable(readEvent.getVenue()).orElse(new EventBriteVenueDto());
		EventBriteAddressDto briteAddress = Optional.ofNullable(briteVenue.getAddress()).orElse(new EventBriteAddressDto());
		
		List<EventDto> eventList = new ArrayList();
		EventDto eventDto = new EventDto();
		eventDto.setEvent_booking(new EventBookingDto());
		eventDto.setEvent_location(new LocationDto());
		
		eventDto.setEvent_type(EABatchConstants.EVENT_TYPE_POSTED);
		eventDto.setEvent_approved(EABatchConstants.EVENT_APPROVED_VAL_1);
		eventDto.setEvent_name(readEvent.getName().getHtml());
		eventDto.setEvent_description(readEvent.getDescription().getHtml());
		eventDto.getEvent_location().setLatitude(briteAddress.getLatitude());
		eventDto.getEvent_location().setLongitude(briteAddress.getLongitude());
		eventDto.getEvent_location().getAddress().setCity(briteAddress.getCity());
		eventDto.getEvent_location().getAddress().setPin(briteAddress.getPostal_code());
		eventDto.getEvent_booking().setUrl(readEvent.getUrl());
		eventDto.setOriginal_event(readEvent);
		
		this.doUpdateAddress(eventDto, briteAddress);
		
		eventList.add(eventDto);
		
		this.doUpdateDateAndTime(eventDto, readEvent, eventList);
		
		//Price
		this.doUpdatePrice(eventDto, readEvent);
		
		//Category
		List<String> categoies = new ArrayList<>();
		categoies.add(readEvent.getCategory().getName());
		processorHelper.addMultiEvents_Cats(categoies, eventList);
		
		return eventList;
	}
	
	private void doUpdatePrice(EventDto eventDto, EventBriteDto readEvent) {
		
		if (readEvent.is_free()) {
			eventDto.setEvent_price(contants.FREE_PRICE_VALUE);
		} else {
			String responseString = briteProvider.getHtmlResponseString(readEvent.getUrl());
			String currency = this.getMatchedGroup(REGEX_PRICE_CURRENCY, responseString, readEvent);
			String lowPrice = this.getMatchedGroup(REGEX_LOW_PRICE, responseString, readEvent);
			String highPrice = this.getMatchedGroup(REGEX_HIGH_PRICE, responseString, readEvent);
			
			String eventPrice = null;
			if(StringUtils.isNotBlank(currency) && StringUtils.isNotBlank(lowPrice)
					&& StringUtils.isNotBlank(highPrice)) {
				try {
					double lowPriceVal = Double.parseDouble(lowPrice);
					double highPriceVal = Double.parseDouble(highPrice);
					if(lowPriceVal != highPriceVal) {
						eventPrice = currency + " " + lowPriceVal + " - " + highPriceVal; 
					} else {
						eventPrice = currency + " " + highPriceVal;
					}
				} catch (Exception e) {
					String value = currency + " " + lowPrice + " - " + highPrice;
					logger.warning("Invalid value for EventPrice : " + value + " | Original EventId : "
							+ readEvent.getId());
				}
			}
			eventDto.setEvent_price(eventPrice);
		}
	}
	
	private String getMatchedGroup(final String regex, final String responseString, EventBriteDto readEvent) {
		String matchedString = null;
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(responseString);
		if (matcher.matches() && matcher.groupCount() >= 1) {
			return matcher.group(1);
		} else {
			logger.warning("No match found for Price Regex : " + regex + " | Original EventId : "
					+ readEvent.getId());
		}
		return matchedString;
	}
	
	private void doUpdateAddress(EventDto eventDto, EventBriteAddressDto briteAddress) {
		
		String streetLine = null;
		if(StringUtils.isNotBlank(briteAddress.getAddress_1()) && StringUtils.isNotBlank(briteAddress.getAddress_2())) {
			streetLine = briteAddress.getAddress_1() + " " + briteAddress.getAddress_2();
		} else if (StringUtils.isNotBlank(briteAddress.getAddress_1())) {
			streetLine = briteAddress.getAddress_1();
		} else if (StringUtils.isNotBlank(briteAddress.getAddress_2())) {
			streetLine = briteAddress.getAddress_2();
		}
		eventDto.getEvent_location().getAddress().setStreet(streetLine);
	}
	
	private void doUpdateDateAndTime(EventDto eventDto, EventBriteDto readEvent, List<EventDto> eventList) {

		BriteDateTime startDateTime = Optional.ofNullable(readEvent.getStart()).orElse(new BriteDateTime());
		BriteDateTime endDateTime = Optional.ofNullable(readEvent.getEnd()).orElse(new BriteDateTime());
		
		Date startDateObj = util.getDateFormatObj(startDateTime.getUtc(), EABatchConstants.DATE_UAT_FORMAT_PATTERN);
		Date endDateObj = util.getDateFormatObj(endDateTime.getUtc(), EABatchConstants.DATE_UAT_FORMAT_PATTERN);
		if (startDateObj != null && endDateObj != null) {
			eventDto.setEvent_start_date(util.getDateUATStr(startDateObj));
			eventDto.setEvent_last_date(util.getDateUATStr(endDateObj));
			eventDto.setEvent_start_time(util.getEATimeFormatStr(startDateObj));
			eventDto.setEvent_end_time(util.getEATimeFormatStr(endDateObj));
		} else {
			logger.warning("In EventBriteProcessor | Unable to get date and time : Event Id : " + readEvent.getId());
			eventList.remove(eventDto);
		}
	}

}
