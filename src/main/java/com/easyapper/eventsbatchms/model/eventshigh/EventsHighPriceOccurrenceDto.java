package com.easyapper.eventsbatchms.model.eventshigh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class EventsHighPriceOccurrenceDto {

	private String date;
	private String end_date;
	private String time;
	private String end_time;
	private int occurrence_id;
	private String group_id;
	private String is_valid;
	private int id;
	
}
