package com.gv4.scheduler.configuration;

import com.gv4.scheduler.listeners.QuestionsUpdateListener;
import com.gv4.scheduler.models.source.SourceSubject;
import com.gv4.scheduler.processors.QuestionsUpdateProcessor;
import com.gv4.scheduler.readers.QuestionsUpdateReader;
import com.gv4.scheduler.writers.QuestionsUpdateWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job makeQuestionsUpdate(QuestionsUpdateListener questionsUpdateListener, Step extractDataFromBinanceStep1){
        return jobBuilderFactory.get("makeQuestionsUpdate")
                .incrementer(new RunIdIncrementer())
                .listener(questionsUpdateListener)
                .flow(extractDataFromBinanceStep1)
                .end()
                .build();
    }

    //TODO add retry policy to allow retry in case of ConnectException
    @Bean
    public Step extractDataFromGithubStep1(QuestionsUpdateReader questionsUpdateReader
            , QuestionsUpdateProcessor questionsUpdateProcessor
            , QuestionsUpdateWriter questionsUpdateWriter){
        return stepBuilderFactory.get("extractDataFromGithubStep1")
                .<SourceSubject, String>chunk(10)
                .reader(questionsUpdateReader)
                //.faultTolerant() // applied to processor and writer only
                //.retryLimit(2)
                //.retry(Exception.class) //add specific (Batch(?)) Exception(?)
                .processor(questionsUpdateProcessor)
                .writer(questionsUpdateWriter)
                .allowStartIfComplete(true)
                .build();
    }

}
