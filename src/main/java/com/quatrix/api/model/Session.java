package com.quatrix.api.model;

import io.swagger.client.model.SessionLoginResp;

import java.util.UUID;

public class Session {

    private final UUID id;

    public Session(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public static Session from(SessionLoginResp sessionLoginResp) {
        return new Session(sessionLoginResp.getSessionId());
    }
}
