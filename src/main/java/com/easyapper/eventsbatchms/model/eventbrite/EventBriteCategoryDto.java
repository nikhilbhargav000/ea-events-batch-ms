package com.easyapper.eventsbatchms.model.eventbrite;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EventBriteCategoryDto {

	private String id;
	private String name;
	private String name_localized;
	private String short_name;
	private String short_name_localized;
	private String resource_uri;
	
}
