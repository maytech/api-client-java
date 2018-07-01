package com.quatrix.api.model;

import io.swagger.client.model.FileRenameResp;

import java.math.BigDecimal;
import java.util.UUID;

public class FileRenameResult {

    private UUID id;
    private BigDecimal created;
    private BigDecimal modified;
    private String name;
    private UUID parentId;
    private Object metadata;
    private BigDecimal size;
    private String type;
    private BigDecimal operations;
    private String oldName;

    public UUID getId() {
        return id;
    }

    public BigDecimal getCreated() {
        return created;
    }

    public BigDecimal getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public Object getMetadata() {
        return metadata;
    }

    public BigDecimal getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getOperations() {
        return operations;
    }

    public String getOldName() {
        return oldName;
    }

    public static FileRenameResult from(FileRenameResp fileRenameResp) {
        final FileRenameResult result = new FileRenameResult();
        result.id = fileRenameResp.getId();
        result.created = fileRenameResp.getCreated();
        result.modified = fileRenameResp.getModified();
        result.name = fileRenameResp.getName();
        result.parentId = fileRenameResp.getParentId();
        result.size = fileRenameResp.getSize();
        result.type = fileRenameResp.getType();
        result.metadata = fileRenameResp.getMetadata();
        result.operations = fileRenameResp.getOperations();
        result.oldName = fileRenameResp.getOldName();

        return result;
    }
}
