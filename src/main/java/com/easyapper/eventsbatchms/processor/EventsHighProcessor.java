package com.easyapper.eventsbatchms.processor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.exception.DateFormatNotSupportedException;
import com.easyapper.eventsbatchms.model.common.Pair;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighDto;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighPriceDto;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighUpcomingOccurrenceDto;
import com.easyapper.eventsbatchms.model.postevent.CategoryDto;
import com.easyapper.eventsbatchms.model.postevent.EventBookingDto;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.model.postevent.LocationDto;
import com.easyapper.eventsbatchms.provider.EAEventsMsProvider;
import com.easyapper.eventsbatchms.reader.CsvFileReader;
import com.easyapper.eventsbatchms.utilities.EABatchUtil;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventsHighProcessor implements ItemProcessor<OrglEventsHighDto, List<EventDto>> {
	
	@Autowired
	EALogger logger;
	@Autowired
	EABatchUtil util;
	@Autowired
	EAEventsMsProvider eaProvider;
	
	@Override
	public List<EventDto> process(OrglEventsHighDto readEvent) throws Exception {
		logger.info("In EventProcessor : process | eventId : " + readEvent.getId());
		List<EventDto> eventList = new ArrayList();
		EventDto eventDto = new EventDto();
		eventDto.setEvent_booking(new EventBookingDto());
		eventDto.setEvent_location(new LocationDto());
		eventDto.setEvent_description(readEvent.getDescription());
		eventDto.setEvent_name(readEvent.getTitle());
		eventDto.setEvent_image_url(readEvent.getImg_url());
		eventDto.setOriginal_event(readEvent);
		//Booking
		eventDto.getEvent_booking().setUrl(readEvent.getBooking_url());
		eventDto.getEvent_booking().setInquiry_url(readEvent.getBooking_enquiry_url());
		//Location
		eventDto.getEvent_location().setLatitude(
				String.valueOf(readEvent.getVenue().getLat()));
		eventDto.getEvent_location().setLongitude(
				String.valueOf(readEvent.getVenue().getLon()));
		eventDto.getEvent_location().getAddress().setCity(
				readEvent.getVenue().getCity());
		eventDto.getEvent_location().getAddress().setStreet(
				readEvent.getVenue().getAddress());
		eventDto.setEvent_type(EABatchConstants.EVENT_TYPE_POSTED);
		eventDto.setEvent_approved(EABatchConstants.EVENT_APPROVED_VAL_1);
		eventDto.setEvent_price(this.getProcessedPrice(readEvent));
		//Add Event
		eventList.add(eventDto);
		//Set Date and Time
		Pair<List<String>, List<String>> pair = this.getTimeArrPair(readEvent);
		try {
			this.addMultiEvents_DateAndTime(pair, readEvent, eventList);
		}catch(DateFormatNotSupportedException e) {
			logger.info("Date or time format not supported"
					+ " | Original Event Id : " + eventDto.getOriginal_event().getId() +""
					+ " | Event : " + eventDto);
			eventList.clear();
			return eventList;
		}
		//Other
		this.addMultiEvents_Cats(readEvent, eventList);
		return eventList;
	}
	
	private void  addMultiEvents_DateAndTime(Pair<List<String>, List<String>> pair, OrglEventsHighDto readEvent, 
			List<EventDto> eventList ) 
					throws DateFormatNotSupportedException, CloneNotSupportedException {
		List<OrglEventsHighUpcomingOccurrenceDto> upcomingOccList = readEvent.getUpcoming_occurrences();
		List<EventDto> toAddEventList = new ArrayList();
		List<EventDto> toRemoveEventList = new ArrayList();
		List<String> startTimeList = pair.getFirst();
		List<String> endTimeList = pair.getSecond();
		for(EventDto eventDto : eventList) {
			toRemoveEventList.add(eventDto);
			for(int indexTime = 0 ; indexTime < startTimeList.size() ; indexTime++) {
				EventDto newEventDto = (EventDto) eventDto.clone();
				String startTime = startTimeList.get(indexTime);
				String endTime = endTimeList.get(indexTime);
				Date minStartDate = null;
				Date maxLastDate = null;
				for(OrglEventsHighUpcomingOccurrenceDto upcomingOcc : upcomingOccList) {
					Date startDate = util.getDateIfInputSupported(upcomingOcc.getDate());
					Date lastDate = util.getDateIfInputSupported(upcomingOcc.getEnd_date());
					if(minStartDate != null && maxLastDate != null) {
						if(minStartDate.after(startDate)) {
							minStartDate = startDate;
						}
						if(maxLastDate.before(lastDate)) {
							maxLastDate = lastDate;
						}
					}else {
						minStartDate = startDate;
						maxLastDate = lastDate;
					}
				}
				newEventDto.setEvent_start_date(util.getDateUATStr(minStartDate));
				newEventDto.setEvent_last_date(util.getDateUATStr(maxLastDate));
				newEventDto.setEvent_start_time(util.geEATimeFormatStr(startTime));
				newEventDto.setEvent_end_time(util.geEATimeFormatStr(endTime));
				toAddEventList.add(newEventDto);
			}
		}
		eventList.removeAll(toRemoveEventList);
		eventList.addAll(toAddEventList);
	}
	
	private Pair<List<String>, List<String>> getTimeArrPair(OrglEventsHighDto readEvent) {
		List<OrglEventsHighUpcomingOccurrenceDto> upcomingOccList = readEvent.getUpcoming_occurrences();
	
		List<String> startTimeArr = new ArrayList<>();
		List<String> endTimeArr = new ArrayList<>();
		Set<String> duplicateCheckSet = new HashSet();
		for(OrglEventsHighUpcomingOccurrenceDto upcomingOcc : upcomingOccList) {
			String startTime = upcomingOcc.getStart_time();
			String endTime = upcomingOcc.getEnd_time();
			String timePairKey = startTime + endTime;
			if(!duplicateCheckSet.contains(timePairKey)) {
				startTimeArr.add(startTime);
				endTimeArr.add(endTime);
				duplicateCheckSet.add(timePairKey);
			}
		}
		Pair<List<String>, List<String>> pair = new Pair<List<String>, List<String>>(startTimeArr, endTimeArr);
		return pair;
	}
	
	/**
	 * Add MultiEvents to eventList
	 * @param readEvent
	 * @param eventList
	 * @throws CloneNotSupportedException
	 */
	private void addMultiEvents_Cats(OrglEventsHighDto readEvent, 
			List<EventDto> eventList) throws CloneNotSupportedException{
		List<String> catList = readEvent.getCats();
		List<EventDto> toRemoveEventList = new ArrayList<>();
		List<EventDto> toAddEventList = new ArrayList();
		for(EventDto eventDto : eventList) {
			toRemoveEventList.add(eventDto);
			for(String cat : catList) {
				String categoryName = this.getCategoryName(cat); 
				if(StringUtils.isNotEmpty(categoryName)) {
					EventDto newEventDto = (EventDto) eventDto.clone();
					newEventDto.setEvent_category(categoryName);
					newEventDto.setEvent_subcategory(cat);
					toAddEventList.add(newEventDto);
				}
			}
		}
		eventList.removeAll(toRemoveEventList);
		eventList.addAll(toAddEventList);
	}
	
	private String getCategoryName(String subCategory) {
		String categoryName = null;
		List<CategoryDto> categories = eaProvider.getCategories();
		if(CollectionUtils.isNotEmpty(categories)) {
			/* Return first match of regex from list */
			CsvFileReader csvFileReader;
			for(CategoryDto categoryDto : categories ) {
				if(StringUtils.isNotEmpty(categoryDto.getRegexFileName()) && 
						StringUtils.isNotEmpty( util.getCategoryRegexFilePath(categoryDto.getRegexFileName()) )) {
					csvFileReader = new CsvFileReader(util.getCategoryRegexFilePath(categoryDto.getRegexFileName()));
					List<String> regexList = csvFileReader.getList();
					if(CollectionUtils.isNotEmpty(regexList)) {
						for(String regex : regexList) {
							try {
								if(regex != null && subCategory != null 
										&& Pattern.matches(regex, subCategory)) {
									return categoryDto.getName();
								}
							}catch(PatternSyntaxException e){
								logger.warning("Invalid regular experession expection : " + regex + ""
										+ " | Category : " + categoryDto);
								continue;
							}
						}
					}else {
						logger.warning("Seems unable to find regex list for category : " + categoryDto); 
					}
				}
			}
		}
		return categoryName;
	}
	
	private String getProcessedPrice(OrglEventsHighDto readEvent){
		String processPrice = "";
		List<OrglEventsHighPriceDto> priceList = readEvent.getPrice();
		double minPrice = priceList.get(0).getValue();
		double maxPrice = priceList.get(0).getValue();
		for(OrglEventsHighPriceDto priceDto : priceList) {
			if(minPrice > priceDto.getValue()) {
				minPrice = priceDto.getValue();
			}
			if(maxPrice < priceDto.getValue()) {
				maxPrice = priceDto.getValue();
			}
		}
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		if(maxPrice != minPrice) {
			processPrice = priceList.get(0).getCurrency() + " " + decimalFormat.format(minPrice) + " - " + 
					decimalFormat.format(maxPrice);
		}else {
			processPrice = priceList.get(0).getCurrency() + " " + decimalFormat.format(minPrice) ;
		}
		return processPrice;
	}
	
}
