package com.easyapper.eventsbatchms.model.eventshigh;

import java.util.List;

import com.easyapper.eventsbatchms.model.common.AbstractOriginalEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class EventsHighDto extends AbstractOriginalEvent {
	
//	private String id;
	private String city;
	private String title;
	private String description;
	private String img_url;
	private List<EventsHighUpcomingOccurrenceDto> upcoming_occurrences;
	private String url;
	private List<String> cats;
	private EventsHighVenueDto venue;
	private List<EventsHighPriceDto> price;
	private String booking_url;
	private String booking_enquiry_url;
	
}
