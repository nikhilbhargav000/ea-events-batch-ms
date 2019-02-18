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

import com.easyapper.eventsbatchms.model.PostEventDto;
import com.easyapper.eventsbatchms.model.ReadEventDto;
import com.easyapper.eventsbatchms.processor.EventProcessor;
import com.easyapper.eventsbatchms.reader.RestEventsReader;
import com.easyapper.eventsbatchms.utilities.EAConstants;
import com.easyapper.eventsbatchms.writer.EventWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	private List<String> urlList = new ArrayList<String>();
	
	@Autowired 
	EAConstants eaContants;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	public ItemReader<ReadEventDto> myReader() {
		urlList.add(EAConstants.DELHI_EVENTS_URL);
		urlList.add(EAConstants.BANGALORE_EVENTS_URL);
		urlList.add(EAConstants.MUMBAI_EVENTS_URL);
		return new RestEventsReader(urlList);
	}
	
	@Bean
	public ItemProcessor<ReadEventDto, PostEventDto> processor() {
		return new EventProcessor();
	}
	
	@Bean
	public ItemWriter<PostEventDto> writer() {
		return new EventWriter();
	}
	
	@Bean 
	public Job importEventsJob() {
		return jobBuilderFactory.get("importEventsJob")
				.incrementer(new RunIdIncrementer())
				.flow(importEventStep())
				.end()
				.build();
	}
	
	@Bean
	public Step importEventStep() {
		return stepBuilderFactory.get("importEventStep")
				.<ReadEventDto, PostEventDto> chunk(10)
				.reader(myReader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
}
