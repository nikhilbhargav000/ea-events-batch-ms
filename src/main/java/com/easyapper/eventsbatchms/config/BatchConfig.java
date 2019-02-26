package com.easyapper.eventsbatchms.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.easyapper.eventsbatchms.listener.EventsJobExecutionListener;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.model.readevent.OrglEventDto;
import com.easyapper.eventsbatchms.processor.EventProcessor;
import com.easyapper.eventsbatchms.reader.RestEventsReader;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.writer.EventWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	private List<String> urlList = new ArrayList<String>();
	
	@Autowired 
	EABatchConstants eaContants;
	
	@Autowired
	RestEventsReader reader;
	
	@Autowired 
	EventProcessor processor;
	
	@Autowired
	EventWriter writer;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	public ItemReader<OrglEventDto> myReader() {
//		reader.addUrl(EABatchConstants.DELHI_EVENTS_URL);
		reader.addUrl(EABatchConstants.BANGALORE_EVENTS_URL);
//		reader.addUrl(EABatchConstants.MUMBAI_EVENTS_URL);
		return this.reader;
	}
	
	public ItemProcessor<OrglEventDto, List<EventDto>> processor() {
		return this.processor;
	}
	
	public ItemWriter<List<EventDto>> writer() {
		return this.writer;
	}
	
	@Bean 
	public Job importEventsJob(EventsJobExecutionListener listener) {
		return jobBuilderFactory.get("importEventsJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(importEventStep())
				.end()
				.build();
	}
	
	public Step importEventStep() {
		return stepBuilderFactory.get("importEventStep")
				.<OrglEventDto, List<EventDto>> chunk(10)
				.reader(myReader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
}
