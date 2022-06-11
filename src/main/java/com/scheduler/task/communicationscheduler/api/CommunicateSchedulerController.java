package com.scheduler.task.communicationscheduler.api;

import com.scheduler.task.communicationscheduler.job.EmailJob;
import com.scheduler.task.communicationscheduler.payload.EmailRequest;
import com.scheduler.task.communicationscheduler.payload.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@RequestMapping("/api")
@RestController
public class CommunicateSchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(CommunicateSchedulerController.class);

    private Scheduler scheduler;

    @PostMapping("/scheduleEmail")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getLocalDateTime(), emailRequest.getZoneId());
            if(dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse scheduleEmailResponse = new EmailResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.badRequest().body(scheduleEmailResponse);
            }

            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            EmailResponse scheduleEmailResponse = new EmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
            return ResponseEntity.ok(scheduleEmailResponse);
        } catch (SchedulerException ex) {
            logger.error("Error scheduling email", ex);

            EmailResponse emailResponse = new EmailResponse(false,
                    "Error scheduling email. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }
    }

    private JobDetail buildJobDetail(EmailRequest scheduleEmailRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


}
