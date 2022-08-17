package com.gv4.scheduler.writers;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.util.List;

@Configuration
@Component
public class QuestionsUpdateWriter implements ItemWriter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsUpdateWriter.class);


    @Override
    @SneakyThrows
    public void write(List<? extends String> list) {

        LOGGER.warn("Writer started!");

        list.forEach(LOGGER::warn);
    }
}
