package com.gv4.scheduler.models.source;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceAnswer {
    private String answer;
    private Boolean correct;
}
