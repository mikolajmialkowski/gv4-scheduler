package com.gv4.scheduler.readers;

import com.gv4.scheduler.models.source.SourceSubject;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Configuration
@Component
public class QuestionsUpdateReader implements ItemReader<SourceSubject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsUpdateReader.class);

    @Override
    @SneakyThrows
    public SourceSubject read() {

        LOGGER.warn("Reader started!");

        return (Math.random()>0.5?SourceSubject.builder()
                .title("APBD")
                .id(String.valueOf(Math.random()*100))
                .data(new ArrayList<>())
                .comments(null)
                .build() : null);
    }
}
