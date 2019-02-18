package com.easyapper.eventsbatchms.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.ReadEventDto;
import com.easyapper.eventsbatchms.model.ReadEventsResponse;

public class RestEventsReader implements ItemReader<ReadEventDto> {

	private List<String> urlList;
	
	private List<ReadEventDto> eventList;
	
	RestTemplate restTemplate ;
	
	/** Is Data refreshed for processing */
	private boolean isEventsUpdated = false;
	
	/** Next Index for data to be fetched */
	private int nextIndex = -1;
	
	public RestEventsReader(List<String> urlList) {
		this.urlList = urlList;
		this.eventList = new ArrayList<>();
		setupRestTemplate();
	}
	
	private void setupRestTemplate() {
		restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList( new MediaType[] {
				MediaType.APPLICATION_JSON, 
				MediaType.APPLICATION_OCTET_STREAM 
			}));
		
		List<HttpMessageConverter<?>> converterList = new ArrayList<>();
		converterList.add(converter);
		restTemplate.setMessageConverters(converterList);
	}
	
	@Override
	public ReadEventDto read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(this.isEventsUpdated == false) {
			fetchRestEventFromApis();
		}
		ReadEventDto eventDto = null;
		if(this.nextIndex < this.eventList.size()) {
		
			System.out.println(nextIndex);
			
			eventDto = this.eventList.get(++nextIndex);
		}
		return eventDto;
	}
	
	private void fetchRestEventFromApis() {
		urlList.stream().forEach((url) -> {
			this.addResponseEvents(this.eventList, url);
		});
	}
	
	private void addResponseEvents(List<ReadEventDto> readEventList, String url) {

		System.out.println(readEventList);
		System.out.println(url);
		
		ResponseEntity<ReadEventsResponse> response = restTemplate.getForEntity(url, ReadEventsResponse.class);
		ReadEventsResponse responseBody = response.getBody();
		List<ReadEventDto> responseEventList = responseBody.getEvents();
		readEventList.addAll(responseEventList);
		
		System.out.println(responseEventList);
		
	}
}
