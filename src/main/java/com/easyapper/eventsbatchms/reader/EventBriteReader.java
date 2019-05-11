package com.easyapper.eventsbatchms.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.eventbrite.EventBriteCategoryDto;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteDto;
import com.easyapper.eventsbatchms.provider.EventBriteProvider;
import com.easyapper.eventsbatchms.utilities.EALogger;

public class EventBriteReader extends EAAbstractReader<EventBriteDto>{

	@Autowired
	EALogger logger;
	@Autowired
	EventBriteProvider briteProvider;
	
	private String categoriesUrl;

	public EventBriteReader(String urlsFilename, String categoriesUrl) {
		super(urlsFilename);
		this.categoriesUrl = categoriesUrl;
	}
	
	@Override
	protected void addResponseEvents(List<EventBriteDto> readEventList, String url) {
		
		
		List<EventBriteDto> events = briteProvider.getEvents(url);
		List<EventBriteDto> toRemoveEvents = new ArrayList<>();
		
		boolean categoiesReadCheck = false;
		
		//Set Category Dto
		if (CollectionUtils.isNotEmpty(events)) {
			Map<String, EventBriteCategoryDto> categoriesMap = new HashMap<>();
			for (EventBriteDto event : events) {
				if (event.getCategory() != null) {
					continue;
				} else if (event.getCategory_id() != null && event.getCategory() == null) {
					if (categoiesReadCheck == false) {
						categoriesMap = this.getCategoryMap();
						categoiesReadCheck = true;
					}
					if (categoriesMap.containsKey(event.getCategory_id()) 
							&& categoriesMap.get(event.getCategory_id()) != null) {
						event.setCategory(categoriesMap.get(event.getCategory_id()));
					}
				} else {
					toRemoveEvents.add(event);
					logger.info("Ignoring event as categoryId is null | Event Id :  " + event.getId());
				}
			}
		}
		
		logger.info("EventBriteReader | Events read count : " + events.size());
		events.removeAll(toRemoveEvents);
		readEventList.addAll(events);
		logger.info("EventBriteReader | Events sent count : " + events.size());
	}
	
	
	private Map<String, EventBriteCategoryDto> getCategoryMap() {
		
		List<EventBriteCategoryDto> categories = briteProvider.getCategories(this.categoriesUrl);
		HashMap<String, EventBriteCategoryDto> categoriesMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(categories))  {
			//Prepare category Map
			for (EventBriteCategoryDto category : categories) {
				categoriesMap.put(category.getId(), category);
			}
		}	
		return categoriesMap;
	}
	
}
