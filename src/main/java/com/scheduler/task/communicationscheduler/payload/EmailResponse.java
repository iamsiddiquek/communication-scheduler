package com.scheduler.task.communicationscheduler.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailResponse implements Serializable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Serial
    private static final long serialVersionUID = -4977687469636639342L;

    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public EmailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


}