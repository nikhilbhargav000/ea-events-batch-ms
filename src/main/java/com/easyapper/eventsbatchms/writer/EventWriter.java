package com.easyapper.eventsbatchms.writer;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.provider.EAEventsMsProvider;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventWriter implements ItemWriter<List<EventDto>> {

	@Autowired
	EALogger logger;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	EAEventsMsProvider eaEventsProvider;
	
	@Override
	public void write(List<? extends List<EventDto>> itemsList) throws Exception {
		logger.info("In Writter : write");
		
		for(List<EventDto> eventList : itemsList ) {
			for(EventDto eventDto : eventList) {
				postEvent(eventDto, 1);
			}
		}
	}
	
	private void postEvent(EventDto eventDto, int tryCount) {
		String url = EABatchConstants.POST_REQUEST_EVENTS_URL;
				
		ResponseEntity<String> response = null;
		try {
			
			eaEventsProvider.postEvent(eventDto);
			
//			response = restTemplate.postForEntity(url, eventDto, String.class);
//			if(response.getStatusCode() == HttpStatus.CREATED) {
//				logger.info("Event created successfully | New Id : " + response.getBody() + ""
//						+ " | Original Event Id : " + eventDto.getOriginal_event().getId());
//			}else if(response.getStatusCode() == HttpStatus.CONFLICT) {
//				logger.info("Event already exist | Failed to created successfully"
//						+ " | Original Event Id : " + eventDto.getOriginal_event().getId() +""
//						+ " | Response code : " + response.getStatusCode() + ""
//						+ " | Event : " + eventDto);
//			}
//			else {
//				logger.info("Failed to created successfully | Original Event Id : " + eventDto.getOriginal_event().getId() +""
//						+ " | Response code : " + response.getStatusCode() + ""
//						+ " | Event : " + eventDto);
//			}
		}catch(HttpClientErrorException  e) {
			logger.warning("HttpClientErrorException | Response code : " + e.getStatusCode() + "| Original Event Id : " + eventDto.getOriginal_event().getId() +""
					+ " | tryCount : " + tryCount
					+ " | Event : " + eventDto );
			if(tryCount <= EABatchConstants.RETRYING_REQUEST_COUNT &&
					e.getStatusCode() != HttpStatus.CONFLICT &&
					e.getStatusCode() != HttpStatus.BAD_REQUEST) {
				sleepCurrentThread(5000);
				postEvent(eventDto, (tryCount+1));
			}
		}catch (ResourceAccessException e) {
			logger.warning("Original Event Id : " + eventDto.getOriginal_event().getId() +""
					+ " | tryCount : " + tryCount
					+ " | Event : " + eventDto, e);
			if(tryCount <= EABatchConstants.RETRYING_REQUEST_COUNT) {
				sleepCurrentThread(5000);
				postEvent(eventDto, (tryCount+1));
			}
		} catch(Exception e) {
			logger.warning("Original Event Id : " + eventDto.getOriginal_event().getId() +""
					+ " | tryCount : " + tryCount
					+ " | Event : " + eventDto, e);
		}
	}
	
	private void sleepCurrentThread(long milisec) {
		try {
			Thread.currentThread().sleep(milisec);
		} catch (InterruptedException e) {
			logger.warning("Exception : ", e);
		}
	}
}
