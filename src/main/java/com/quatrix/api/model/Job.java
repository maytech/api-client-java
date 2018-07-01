package com.quatrix.api.model;

import io.swagger.client.model.JobResp;

import java.util.UUID;

public class Job {

    private final UUID id;

    public Job(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public static Job from(JobResp jobResp) {
        return new Job(jobResp.getJobId());
    }
}
