package com.easyapper.eventsbatchms.model.eventshigh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class OrglEventsHighUpcomingOccurrenceDto {
	
	private long occurrence_id;
	private String date;
	private String end_date;
	private String start_time;
	private String end_time;
	private int single_occurrence;
	private String timezone;
	private boolean enable_ticketing;
	

}
