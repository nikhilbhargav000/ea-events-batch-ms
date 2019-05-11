package com.easyapper.eventsbatchms.model.eventbrite;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EventBritePaginationDto {
	
	private int object_count;
	private int page_number;
	private int page_size;
	private int page_count;
	private boolean has_more_items;
	
}
