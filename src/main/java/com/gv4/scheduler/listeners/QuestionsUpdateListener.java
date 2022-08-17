package com.gv4.scheduler.listeners;

import com.gv4.scheduler.utils.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class QuestionsUpdateListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsUpdateListener.class);
    private final EmailService emailService;

    public QuestionsUpdateListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void beforeJob(JobExecution jobExecution){
        LOGGER.warn("Job with ID: {} ,started at: {} "
                , jobExecution.getJobId()
                , jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution){
        LOGGER.warn("Job with ID: {}, finished at: {}, with status: {}"
                , jobExecution.getJobId()
                , jobExecution.getEndTime()
                , jobExecution.getExitStatus());

        if (jobExecution.getStatus() == BatchStatus.FAILED)
            emailService.sendErrorMessage(jobExecution.getAllFailureExceptions().toString());

    }
}
