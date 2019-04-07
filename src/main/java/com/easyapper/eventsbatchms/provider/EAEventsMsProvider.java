package com.easyapper.eventsbatchms.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EAEventsMsProvider {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	EALogger logger;
	
	public void postEvent(EventDto eventDto) throws HttpClientErrorException, Exception{
		String url = EABatchConstants.POST_REQUEST_EVENTS_URL;
		
		ResponseEntity<String> response = restTemplate.postForEntity(url, eventDto, String.class);
		if(response.getStatusCode() == HttpStatus.CREATED) {
			logger.info("Event created successfully | New Id : " + response.getBody() + ""
					+ " | Original Event Id : " + eventDto.getOriginal_event().getId());
		}else if(response.getStatusCode() == HttpStatus.CONFLICT) {
			logger.info("Event already exist | Failed to created successfully"
					+ " | Original Event Id : " + eventDto.getOriginal_event().getId() +""
					+ " | Response code : " + response.getStatusCode() + ""
					+ " | Event : " + eventDto);
		}
		else {
			logger.info("Failed to created successfully | Original Event Id : " + eventDto.getOriginal_event().getId() +""
					+ " | Response code : " + response.getStatusCode() + ""
					+ " | Event : " + eventDto);
		}
	}
	
	
}
