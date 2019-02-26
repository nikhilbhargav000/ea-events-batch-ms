package com.easyapper.eventsbatchms.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.utilities.EALogger;

@Component
@EnableScheduling
public class EventsJobScheduler {
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired
	Job job;
	
	@Autowired
	EALogger logger;
	
	@Scheduled(cron="1 * * * * *")
	public void eventJobSchduler() {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", 
				System.currentTimeMillis()).toJobParameters();
		try {
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);
			logger.info("JOB STATUS : " + jobExecution.getStatus());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			logger.warning("Error in Schduling Events Job", e);
		}
	}
}
