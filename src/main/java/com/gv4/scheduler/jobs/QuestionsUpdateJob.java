package com.gv4.scheduler.jobs;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class QuestionsUpdateJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsUpdateJob.class);

    private final Job questionsUpdate;
    private final JobLauncher jobLauncher;

    public QuestionsUpdateJob(@Qualifier("makeQuestionsUpdate") Job questionsUpdate
            , JobLauncher jobLauncher) {
        this.questionsUpdate = questionsUpdate;
        this.jobLauncher = jobLauncher;
    }

    @SneakyThrows
    @Scheduled(cron = "* * * * * ?")
    public void updateQuestions(){
        LOGGER.info("QuestionsUpdateJob executed at: {}", LocalDateTime.now());
        jobLauncher.run(questionsUpdate, getJobParameters());
    }

    public JobParameters getJobParameters(){
        return new JobParametersBuilder()
                .addString("JOB TIMESTAMP", String.valueOf(LocalDateTime.now()))
                .toJobParameters();
    }
}
