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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.easyapper.eventsbatchms.listener.EventsJobExecutionListener;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighDto;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.processor.EventsHighProcessor;
import com.easyapper.eventsbatchms.reader.CsvFileReader;
import com.easyapper.eventsbatchms.reader.EventsHighReader;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.writer.EventWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Autowired 
	EABatchConstants eaContants;
	@Autowired
	EventsHighReader reader;
	@Autowired 
	EventsHighProcessor processor;
	@Autowired
	EventWriter writer;
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
//	@Value("#{"
//			+ "'${batch.url.list}'.split(',')"
//			+ "}")
//	List<String> urlList;
	
	
	
	public ItemReader<OrglEventsHighDto> myReader() {
//		reader.addUrl(EABatchConstants.DELHI_EVENTS_URL);
//		reader.addUrl(EABatchConstants.BANGALORE_EVENTS_URL);
//		reader.addUrl(EABatchConstants.MUMBAI_EVENTS_URL);
		
//		urlList.stream().forEach((url)->{reader.addUrl(url.trim());});
		
		
		reader.refreshUrlsFromCsvFile();
		return this.reader;
	}
	
	public ItemProcessor<OrglEventsHighDto, List<EventDto>> processor() {
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
				.<OrglEventsHighDto, List<EventDto>> chunk(10)
				.reader(myReader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
}
