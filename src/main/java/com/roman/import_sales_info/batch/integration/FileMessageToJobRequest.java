package com.roman.import_sales_info.batch.integration;

import lombok.Setter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Setter
public class FileMessageToJobRequest {
    private Job job;
    private String filename = "input.filename";

    @Transformer
    public JobLaunchRequest jobLaunchRequest(Message<File> fileMessage){
        var jobParams = new JobParametersBuilder();
        jobParams.addString(filename, fileMessage.getPayload().getAbsolutePath());
        jobParams.addLong("Time", System.currentTimeMillis());
        return new JobLaunchRequest(job, jobParams.toJobParameters());
    }
}
