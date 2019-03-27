package com.easyapper.eventsbatchms.model.eventshigh;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class EventsHighResponse {

	List<OrglEventsHighDto> events;
	
}
