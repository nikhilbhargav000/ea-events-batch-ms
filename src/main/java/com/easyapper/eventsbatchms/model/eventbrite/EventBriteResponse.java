package com.easyapper.eventsbatchms.model.eventbrite;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EventBriteResponse {
	
	private EventBritePaginationDto pagination;
	private List<EventBriteDto> events;
	
}
