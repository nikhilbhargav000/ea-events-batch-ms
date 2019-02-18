package com.easyapper.eventsbatchms.processor;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.item.ItemProcessor;

import com.easyapper.eventsbatchms.model.PostEventDto;
import com.easyapper.eventsbatchms.model.ReadEventDto;

public class EventProcessor implements ItemProcessor<ReadEventDto, PostEventDto> {

	@Override
	public PostEventDto process(ReadEventDto item) throws Exception {
		
		System.out.println(item);
		
		return null;
	}

}
