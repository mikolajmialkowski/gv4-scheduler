package com.gv4.scheduler.processors;

import com.gv4.scheduler.models.source.SourceSubject;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuestionsUpdateProcessor implements ItemProcessor<SourceSubject, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsUpdateProcessor.class);


    @Override
    @SneakyThrows
    public String process(SourceSubject sourceSubject){

        LOGGER.warn("Processor started!");

        if (Math.random()>0.5)
            throw new RuntimeException("test exception");

        return sourceSubject.getTitle();
    }
}
