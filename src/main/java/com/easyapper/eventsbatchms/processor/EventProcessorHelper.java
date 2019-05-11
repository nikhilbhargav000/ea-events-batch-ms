package com.easyapper.eventsbatchms.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.model.eventshigh.EventsHighDto;
import com.easyapper.eventsbatchms.model.postevent.CategoryDto;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.provider.EAEventsMsProvider;
import com.easyapper.eventsbatchms.reader.CsvFileReader;
import com.easyapper.eventsbatchms.utilities.EABatchUtil;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventProcessorHelper {

	@Autowired
	EALogger logger;
	@Autowired
	EABatchUtil util;
	@Autowired
	EAEventsMsProvider eaProvider;
	
	
	/**
	 * Add MultiEvents to eventList
	 * @param readEvent
	 * @param eventList
	 * @throws CloneNotSupportedException
	 */
	public void addMultiEvents_Cats(List<String> catList , 
			List<EventDto> eventList) throws CloneNotSupportedException{
//		List<String> catList = readEvent.getCats();
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
}
