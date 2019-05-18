package com.easyapper.eventsbatchms.provider;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.easyapper.eventsbatchms.model.eventbrite.EventBriteCategoryDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteCategoryResponse;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBritePaginationDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteResponse;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventBriteProvider {

	@Autowired
	@Qualifier("eaRestTemplate")
	RestTemplate restTemplate;
	@Autowired
	@Qualifier("eaStringRestTemplate")
	RestTemplate stringRestTemplate;
	@Autowired
	EALogger logger;
	
	public String getHtmlResponseString(String eventUrl) {
		String responseString = null;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		HttpEntity requestEntity = new HttpEntity<>(headers);
		
		try {
			URI eventUri = new URI(eventUrl);
			logger.info("EventBriteProvider | connecting to url : " + eventUri.toString());
			ResponseEntity<String> response = stringRestTemplate.exchange(eventUri, HttpMethod.GET, requestEntity,
					String.class);
			if (response != null) {
				responseString = response.getBody();
			}
		} catch(Exception e) {
			logger.warning("Error while connecting to URL : " + eventUrl, e);
		}
					
		return responseString;
	}
	
	public List<EventBriteCategoryDto> getCategories(String categoryUrl) {
		List<EventBriteCategoryDto> categories = new ArrayList<>();
		final String PAGE_QUERY_PARAM_KEY = "page";
		
 		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity httpEntity = new HttpEntity(headers);
		int pageNo = 1;
		EventBritePaginationDto pagination = null;
		do {
			try {
				URI categoriesUri = UriComponentsBuilder.fromUriString(categoryUrl)
						.queryParam(PAGE_QUERY_PARAM_KEY, String.valueOf(pageNo)).build().toUri();
				logger.info("EventBriteProvider | connecting to url : " + categoriesUri.toString());
				ResponseEntity<EventBriteCategoryResponse> response =  restTemplate.exchange(categoriesUri, HttpMethod.GET, 
						httpEntity, EventBriteCategoryResponse.class);
				EventBriteCategoryResponse briteCategoryResponse = response.getBody();
				if(briteCategoryResponse != null 
						&& CollectionUtils.isNotEmpty(briteCategoryResponse.getCategories())
						&& briteCategoryResponse.getPagination() != null) {
					categories.addAll(briteCategoryResponse.getCategories());
					pagination = briteCategoryResponse.getPagination();
				} else {
					logger.warning("Unable fetch categories from Event Brite API");
					pagination = null;
				}
				pageNo++;
			} catch (Exception e) {
				logger.warning("Error while fetching categories from Event Brite API", e);
				pagination = null;
			}
		} while (pagination != null && pagination.isHas_more_items());
		return categories;
	}
	
	public List<EventBriteDto> getEvents(String url) {
		List<EventBriteDto> events = new ArrayList<>();
		final String PAGE_QUERY_PARAM_KEY = "page";
		
		//Request
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity httpEntity = new HttpEntity<String>(headers);
		int pageNumber = 1;
		EventBritePaginationDto pagination = null;
		do {
			try {
				URI eventsUri = UriComponentsBuilder.fromUriString(url)
						.queryParam(PAGE_QUERY_PARAM_KEY, String.valueOf(pageNumber)).build().toUri();
				logger.info("EventBriteProvider | connecting to url : " + eventsUri.toString());
				ResponseEntity<EventBriteResponse> response = restTemplate.exchange(eventsUri, HttpMethod.GET,
						httpEntity, EventBriteResponse.class);
				if (response != null && CollectionUtils.isNotEmpty(response.getBody().getEvents())) {
					events.addAll(response.getBody().getEvents());
					pagination = response.getBody().getPagination();
				} else {
					logger.warning("Unable while fetching events from Event Brite API");
					pagination = null;
				}
				pageNumber++;
			} catch (Exception e) {
				logger.warning("Error while fetching events from Event Brite API", e);
				pagination = null;
			}
		} while(pagination != null && pagination.isHas_more_items());
		return events;
	}
}
