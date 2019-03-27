package com.easyapper.eventsbatchms.model.eventshigh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class OrglEventsHighVenueDto {
	private String name;
	private String address;
	private double lat;
	private double lon;
	private String city;
	
}
