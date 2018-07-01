package com.quatrix.api.model;

import io.swagger.client.model.UploadFinalizeResp;

import java.math.BigDecimal;
import java.util.UUID;

public class UploadResult {

    private UUID id;
    private UUID parentId;
    private BigDecimal size;
    private BigDecimal modified;

    public UUID getId() {
        return id;
    }

    public UUID getParentId() {
        return parentId;
    }

    public BigDecimal getSize() {
        return size;
    }

    public BigDecimal getModified() {
        return modified;
    }

    public static UploadResult from(UploadFinalizeResp uploadFinalizeResp) {
        UploadResult result = new UploadResult();
        result.id = uploadFinalizeResp.getId();
        result.parentId = uploadFinalizeResp.getParentId();
        result.size = uploadFinalizeResp.getSize();
        result.modified = uploadFinalizeResp.getModified();

        return result;
    }
}
