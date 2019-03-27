package com.easyapper.eventsbatchms.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.eventshigh.EventsHighResponse;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighDto;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventsHighReader implements ItemReader<OrglEventsHighDto> {

	@Autowired
	EALogger logger;
	
	private List<String> eventsHighUrlList;
	
	private List<OrglEventsHighDto> eventList;
	
	/* Read Urls from csv file */
	@Value("${eventshigh.urls.filepath}")
	String eventshighFilename;
	
	RestTemplate restTemplate ;
	
	/** Is Data refreshed for processing */
	private boolean isEventsListRefreshed = false;
	
	/** Next Index for data to be fetched */
	private int nextIndex = -1;
	
	public EventsHighReader() {
		this.eventsHighUrlList = new ArrayList<>();
		this.eventList = new ArrayList<>();
		setupRestTemplate();
	}

	public void addUrl(String urlList) {
		this.eventsHighUrlList.add(urlList);
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
	public OrglEventsHighDto read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(this.isEventsListRefreshed() == false) {
			fetchRestEventFromApis();
		}
		OrglEventsHighDto eventDto = null;
		if((this.nextIndex + 1) < this.eventList.size()) {
			eventDto = this.eventList.get(++nextIndex);
		}
		return eventDto;
	}
	
	private boolean isEventsListRefreshed() {
		return this.isEventsListRefreshed;
	}
	
	private void setEventsListRefreshed() {
		this.isEventsListRefreshed = true;
	}
	
	public void resetReader() {
		this.isEventsListRefreshed = false;
		this.eventList.clear();
		this.nextIndex = -1;
		refreshUrlsFromCsvFile();
	}
	
	public void refreshUrlsFromCsvFile() {
		CsvFileReader csvFileReader = new CsvFileReader(eventshighFilename);
		this.eventsHighUrlList = csvFileReader.getList();
		this.eventsHighUrlList.stream().forEach((url)->{url.trim();});
	}
	
	private void fetchRestEventFromApis() {
		eventsHighUrlList.stream().forEach((url) -> {
			this.addResponseEvents(this.eventList, url);
		});
		this.setEventsListRefreshed();
	}
	
	private void addResponseEvents(List<OrglEventsHighDto> readEventList, String url) {
		try {
			ResponseEntity<EventsHighResponse> response = restTemplate.getForEntity(url, EventsHighResponse.class);
			EventsHighResponse responseBody = response.getBody();
			List<OrglEventsHighDto> responseEventList = responseBody.getEvents();
			readEventList.addAll(responseEventList);
		}catch (IllegalArgumentException e) {
			logger.warning("Seems like invalid URL : " + url, e);
		}catch (Exception e) {
			logger.warning("Error while communicating with URL : " + url, e);
		}
	}
}
