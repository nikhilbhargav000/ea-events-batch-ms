package com.easyapper.eventsbatchms.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
		
		String streetLine = briteAddress.getAddress_1() + " " + briteAddress.getAddress_2();
		eventDto.getEvent_location().getAddress().setStreet(streetLine);
		
		eventList.add(eventDto);
		
		doUpdateDateAndTime(eventDto, readEvent, eventList);
		
		//Category
		List<String> categoies = new ArrayList<>();
		categoies.add(readEvent.getCategory().getName());
		processorHelper.addMultiEvents_Cats(categoies, eventList);
		
		return eventList;
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
