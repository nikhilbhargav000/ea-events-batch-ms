package com.easyapper.eventsbatchms.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.easyapper.eventsbatchms.listener.EventsJobExecutionListener;
import com.easyapper.eventsbatchms.model.eventbrite.EventBriteDto;
import com.easyapper.eventsbatchms.model.eventshigh.EventsHighDto;
import com.easyapper.eventsbatchms.model.postevent.EventDto;
import com.easyapper.eventsbatchms.processor.EventBriteProcessor;
import com.easyapper.eventsbatchms.processor.EventsHighProcessor;
import com.easyapper.eventsbatchms.reader.CsvFileReader;
import com.easyapper.eventsbatchms.reader.EAAbstractReader;
import com.easyapper.eventsbatchms.reader.EventBriteReader;
import com.easyapper.eventsbatchms.reader.EventsHighReader;
import com.easyapper.eventsbatchms.utilities.EABatchConstants;
import com.easyapper.eventsbatchms.writer.EventWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Value("${eventshigh.urls.filepath}")
	private String eventshighUrlsFileName;
	@Value("${eventbrite.urls.filepath}")
	private String eventBriteUrlsFileName;
	@Value("${eventbrite.categories.url}")
	private String eventBriteCategoriesUrl;

	private List<EAAbstractReader> readerList;
	
	@Autowired 
	EABatchConstants eaContants;
	@Autowired
	ApplicationContext appContext;
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	
	@PostConstruct
	public void registerReaders() {
		if(readerList == null) {
			readerList = new ArrayList<>();
		}
		readerList.clear();
		readerList.add((EventsHighReader)createEventsHighReader());
		readerList.add((EventBriteReader)createEventBriteReader());
	}	
	
	public List<EAAbstractReader> getReaderList() {
		return readerList;
	}

	@Bean
	public ItemReader<EventsHighDto> createEventsHighReader() {
		EventsHighReader reader = new EventsHighReader(this.eventshighUrlsFileName);
		reader.refreshUrlsFromCsvFile();
		return reader;
	}
	
	@Bean
	public ItemReader<EventBriteDto> createEventBriteReader() {
		EventBriteReader briteReader = new EventBriteReader(this.eventBriteUrlsFileName, 
				this.eventBriteCategoriesUrl);
		briteReader.refreshUrlsFromCsvFile();
		return briteReader;
	}
	
	public ItemProcessor<EventsHighDto, List<EventDto>> createEventsHighProcessor() {
		return appContext.getBean(EventsHighProcessor.class);
	}
	
	public ItemProcessor<EventBriteDto, List<EventDto>> createEventBriteProcessor() {
		return appContext.getBean(EventBriteProcessor.class);
	}
	
	
	public ItemWriter<List<EventDto>> writer() {
		return appContext.getBean(EventWriter.class);
	}
	
	@Bean 
	public Job importEventsJob(EventsJobExecutionListener listener) {
		return jobBuilderFactory.get("importEventsJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.start(importEventsHighStep())
				.next(importEventBriteStep())
				.build();
	}
	
	public Step importEventsHighStep() {
		return stepBuilderFactory.get("eventsHighStep")
				.<EventsHighDto, List<EventDto>> chunk(10)
				.reader(createEventsHighReader())
				.processor(createEventsHighProcessor())
				.writer(writer())
				.build();
	}
	
	public Step importEventBriteStep() {
		return stepBuilderFactory.get("eventBriteStep")
				.<EventBriteDto, List<EventDto>>chunk(10)
				.reader(createEventBriteReader())
				.processor(createEventBriteProcessor())
				.writer(writer())
				.build();
	}
	
}
