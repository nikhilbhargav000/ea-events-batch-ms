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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.readevent.OrglEventDto;
import com.easyapper.eventsbatchms.model.readevent.ReadEventsResponse;

@Component
public class RestEventsReader implements ItemReader<OrglEventDto> {

	private List<String> urlList;
	
	private List<OrglEventDto> eventList;
	
	RestTemplate restTemplate ;
	
	/** Is Data refreshed for processing */
	private boolean isEventsListRefreshed = false;
	
	/** Next Index for data to be fetched */
	private int nextIndex = -1;
	
	public RestEventsReader() {
		this.urlList = new ArrayList<>();
		this.eventList = new ArrayList<>();
		setupRestTemplate();
	}

	public void addUrl(String urlList) {
		this.urlList.add(urlList);
	}

	private void setupRestTemplate() {
		this.restTemplate = new RestTemplate();
		//For Octet Stream support
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList( new MediaType[] {
				MediaType.APPLICATION_JSON, 
				MediaType.APPLICATION_OCTET_STREAM 
			}));
		List<HttpMessageConverter<?>> converterList = new ArrayList<>();
		converterList.add(converter);
		restTemplate.setMessageConverters(converterList);
	}

	/**
	 *  Read Events from Endpoints
	 */
	@Override
	public OrglEventDto read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(this.isEventsListRefreshed() == false) {
			fetchRestEventFromApis();
		}
		OrglEventDto eventDto = null;
		if((this.nextIndex + 1) < this.eventList.size()) {
			eventDto = this.eventList.get(++nextIndex);
		}
		return eventDto;
	}
	
	public boolean isEventsListRefreshed() {
		return this.isEventsListRefreshed;
	}
	
	public void setEventsListRefreshed() {
		this.isEventsListRefreshed = true;
	}
	
	public void resetEventsList() {
		this.isEventsListRefreshed = false;
		this.eventList.clear();
		this.nextIndex = -1;
	}
	
	private void fetchRestEventFromApis() {
		urlList.stream().forEach((url) -> {
			this.addResponseEvents(this.eventList, url);
		});
		this.setEventsListRefreshed();
	}
	
	private void addResponseEvents(List<OrglEventDto> readEventList, String url) {
		ResponseEntity<ReadEventsResponse> response = restTemplate.getForEntity(url, ReadEventsResponse.class);
		ReadEventsResponse responseBody = response.getBody();
		List<OrglEventDto> responseEventList = responseBody.getEvents();
		readEventList.addAll(responseEventList);
	}
}
