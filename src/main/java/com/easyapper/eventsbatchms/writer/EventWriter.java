package com.easyapper.eventsbatchms.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.easyapper.eventsbatchms.model.PostEventDto;

public class EventWriter implements ItemWriter<PostEventDto> {

	@Override
	public void write(List<? extends PostEventDto> items) throws Exception {
		
	}

}
