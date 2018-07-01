package com.quatrix.api.model;

import io.swagger.client.model.IdsResp;

import java.util.List;
import java.util.UUID;

public class FileIds {

    private final List<UUID> ids;

    public FileIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public static FileIds from(IdsResp idsResp) {
        return new FileIds(idsResp.getIds());
    }
}
