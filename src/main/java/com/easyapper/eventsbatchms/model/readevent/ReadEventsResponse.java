package com.easyapper.eventsbatchms.model.readevent;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class ReadEventsResponse {

	List<OrglEventDto> events;
	
}
