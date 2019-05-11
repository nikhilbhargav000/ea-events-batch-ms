package com.easyapper.eventsbatchms.provider;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.easyapper.eventsbatchms.model.postevent.CategoriesResponseDto;
import com.easyapper.eventsbatchms.model.postevent.CategoryDto;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EAEventsMsProvider {

	@Autowired
	@Qualifier("eaStringRestTemplate")
	RestTemplate restTemplateForString;
	
	@Autowired
	@Qualifier("eaRestTemplate")
	RestTemplate restTemplateForJson;
	
	@Autowired
	EALogger logger;
	
	public void postEvent(EventDto eventDto) throws HttpClientErrorException, Exception{
		String url = EABatchConstants.EA_POST_REQUEST_EVENTS_URL;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
		HttpEntity requestEntity = new HttpEntity(eventDto, headers);
		ResponseEntity<String> response = restTemplateForString.exchange(url, HttpMethod.POST, requestEntity, String.class);
		
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
	
	public List<CategoryDto> getCategories() {
		String url = EABatchConstants.EA_GET_CATEGORIES_URL;
		List<CategoryDto> categories = new ArrayList<>();
		try {
			ResponseEntity<CategoriesResponseDto> categoriesResponse  = null;
			Map<String, String> params = new HashMap<>();
			int page = 1;
			do {
				URI categoryUri = UriComponentsBuilder.fromUriString(url)
						.queryParam("page", String.valueOf(page++)).build().toUri();
				categoriesResponse = restTemplateForJson.getForEntity(categoryUri, CategoriesResponseDto.class);
				if(categoriesResponse != null && 
					CollectionUtils.isNotEmpty(categoriesResponse.getBody().getCategories())) {
				categories.addAll(categoriesResponse.getBody().getCategories());
				}
			} while(categoriesResponse != null && 
					CollectionUtils.isNotEmpty(categoriesResponse.getBody().getCategories()));		
		}catch(Exception e) {
			logger.warning("Error while connecting to url  : " + url, e);
		}
		return categories;
	}
	
}
