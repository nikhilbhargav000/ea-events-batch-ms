package com.easyapper.eventsbatchms.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.reader.RestEventsReader;
import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
public class EventsJobExecutionListener extends JobExecutionListenerSupport {

	@Autowired
	EALogger logger;
	
	@Autowired
	ApplicationContext appContext;
	
	@Autowired
	RestEventsReader reader;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		super.beforeJob(jobExecution);
		logger.info("Before job : JOB ID : " + jobExecution.getJobId());
//		logger.info("Before job : JOB ID : " + jobExecution.getJobId() +" : "
//				+ "" + jobExecution);
		
		System.out.println("Reader : " + reader);
	}
			
	@Override
	public void afterJob(JobExecution jobExecution) {
		super.afterJob(jobExecution);
		logger.info("After job : JOB ID : " + jobExecution.getJobId() + ""
				+ "\nSTATUS : " + jobExecution.getStatus() );
		
		if(this.reader == null) {
			reader = appContext.getBean(RestEventsReader.class);
		}
		reader.resetEventsList();
	}
	
}
