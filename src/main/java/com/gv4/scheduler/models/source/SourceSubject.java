package com.gv4.scheduler.models.source;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class SourceSubject {

    private String title;
    private String id;
    private List<SourceSubject> data;
    private List<String> comments;

}
