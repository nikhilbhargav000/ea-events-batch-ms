package com.easyapper.eventsbatchms.model;

import java.util.List;

public class ReadEventsResponse {

	List<ReadEventDto> events;
	//Constructor
	public ReadEventsResponse(List<ReadEventDto> events) {
		super();
		this.events = events;
	}
	public ReadEventsResponse() {
		super();
	}
	public List<ReadEventDto> getEvents() {
		return events;
	}
	public void setEvents(List<ReadEventDto> events) {
		this.events = events;
	}
}
