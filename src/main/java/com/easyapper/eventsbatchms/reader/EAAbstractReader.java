package com.easyapper.eventsbatchms.reader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import com.easyapper.eventsbatchms.utilities.EALogger;

public abstract class EAAbstractReader<EventBeanType> implements ItemReader<EventBeanType> {

	protected List<String> eventUrlsList;
	
	protected List<EventBeanType> eventList;
	
	protected String urlsFilename;
	
	/** Is Data refreshed for processing */
	protected boolean isEventsListRefreshed = false;
	
	/** Next Index for data to be fetched */
	protected int nextIndex = -1;
	
	@Autowired
	EALogger logger;
	
	public EAAbstractReader(String urlsFilename) {
		this.eventUrlsList = new ArrayList<>();
		this.eventList = new ArrayList<>();
		this.urlsFilename = urlsFilename;
	}
	
	@Override
	public EventBeanType read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		synchronized (this.eventList) {
			if(this.isEventsListRefreshed() == false) {
				fetchRestEventFromApis();
			}
			EventBeanType eventDto = null;
			if((this.nextIndex + 1) < this.eventList.size()) {
				eventDto = this.eventList.get(++nextIndex);
			}
			return eventDto;
		}
	}
	
	protected void fetchRestEventFromApis() {
		if(CollectionUtils.isEmpty(eventUrlsList)) {
			logger.warning("Unable to find urls in file : " + this.urlsFilename);
		}
		this.setEventsListRefreshed();
		eventUrlsList.stream().forEach((url) -> {
			this.addResponseEvents(this.eventList, url);
		});
	}
	/**
	 * Read events from Api and add them in 'readEventList' for each 'url'
	 * 
	 * @param readEventList : 
	 * @param url
	 */
	protected abstract void addResponseEvents(List<EventBeanType> readEventList, String url) ;
	
	protected boolean isEventsListRefreshed() {
		return this.isEventsListRefreshed;
	}
	
	protected void setEventsListRefreshed() {
		this.isEventsListRefreshed = true;
	}
	
	public void resetReader() {
		this.isEventsListRefreshed = false;
		this.eventList.clear();
		this.nextIndex = -1;
		refreshUrlsFromCsvFile();
	}
	public void refreshUrlsFromCsvFile() {
		CsvFileReader csvFileReader = new CsvFileReader(this.urlsFilename);
		this.eventUrlsList = csvFileReader.getList();
		this.eventUrlsList.stream().forEach((url)->{url.trim();});
	}
	
}
