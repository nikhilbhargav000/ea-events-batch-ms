package com.easyapper.eventsbatchms.model.eventbrite;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EventBriteVenueDto {
	
	private String id;
	private EventBriteAddressDto address;
	private String resource_uri;
	private String age_restriction;
	private String capacity;
	private String name;
	private String latitude;
	private String longitude;
	
}
