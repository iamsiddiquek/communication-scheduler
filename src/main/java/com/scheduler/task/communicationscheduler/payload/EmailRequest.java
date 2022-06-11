package com.scheduler.task.communicationscheduler.payload;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Setter
@Getter
public class EmailRequest implements Serializable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Serial
    private static final long serialVersionUID = -5477818022673625362L;

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
    @NotNull
    private LocalDateTime localDateTime;
    @NotNull
    private ZoneId zoneId;


}
