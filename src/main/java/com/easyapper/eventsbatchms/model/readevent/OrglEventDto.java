package com.easyapper.eventsbatchms.model.readevent;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class OrglEventDto {
	
	private String id;
	private String city;
	private String title;
	private String description;
	private String img_url;
	private List<OrglEventUpcomingOccurrenceDto> upcoming_occurrences;
	private String url;
	private List<String> cats;
	private OrglEventVenueDto venue;
	private List<OrglEventPriceDto> price;
	private String booking_url;
	private String booking_enquiry_url;
	
}
