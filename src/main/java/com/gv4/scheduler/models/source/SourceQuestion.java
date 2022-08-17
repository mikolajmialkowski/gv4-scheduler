package com.gv4.scheduler.models.source;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SourceQuestion {

    private String question;
    private String id;
    private String numberOfComments;
    private List<SourceAnswer> answers;
}
