package com.easyapper.eventsbatchms.listener;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.config.BatchConfig;
import com.easyapper.eventsbatchms.reader.EventsHighReader;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventsJobExecutionListener extends JobExecutionListenerSupport {

	@Autowired
	EALogger logger;
	
	@Autowired
	ApplicationContext appContext;
	
	BatchConfig batchConfig;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		super.beforeJob(jobExecution);
		logger.info("Before job : JOB ID : " + jobExecution.getJobId());
	}
			
	@Override
	public void afterJob(JobExecution jobExecution) {
		super.afterJob(jobExecution);
		logger.info("After job : JOB ID : " + jobExecution.getJobId() + ""
				+ "\nSTATUS : " + jobExecution.getStatus() );
		
		if (this.batchConfig == null) {
			batchConfig = appContext.getBean(BatchConfig.class);
		}
		if (CollectionUtils.isEmpty(batchConfig.getReaderList())) {
			batchConfig.registerReaders();
		} else {
			batchConfig.getReaderList().stream().forEach((eventReader) -> {
				eventReader.resetReader();
			});
		}
	}
	
}
