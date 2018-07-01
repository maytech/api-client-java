package com.quatrix.api.model;

import com.google.common.base.Function;
import com.quatrix.api.utils.CollectionUtils;
import io.swagger.client.model.FileMetadataGetResp;
import io.swagger.client.model.FileResp;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class FileMetadata {

    private UUID id;
    private BigDecimal gid;
    private BigDecimal uid;
    private BigDecimal created;
    private String name;
    private BigDecimal modified;
    private UUID parentId;
    private BigDecimal size;
    private String type;
    private Object metadata;
    private BigDecimal operations;
    private String subType;
    private List<FileInfo> content;

    public UUID getId() {
        return id;
    }

    public BigDecimal getGid() {
        return gid;
    }

    public BigDecimal getUid() {
        return uid;
    }

    public BigDecimal getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getModified() {
        return modified;
    }

    public UUID getParentId() {
        return parentId;
    }

    public BigDecimal getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public Object getMetadata() {
        return metadata;
    }

    public BigDecimal getOperations() {
        return operations;
    }

    public String getSubType() {
        return subType;
    }

    public List<FileInfo> getContent() {
        return content;
    }

    public static FileMetadata from(FileMetadataGetResp fileMetadataGetResp) {
        final FileMetadata metadata = new FileMetadata();
        metadata.id = fileMetadataGetResp.getId();
        metadata.gid = fileMetadataGetResp.getGid();
        metadata.uid = fileMetadataGetResp.getUid();
        metadata.created = fileMetadataGetResp.getCreated();
        metadata.modified = fileMetadataGetResp.getModified();
        metadata.name = fileMetadataGetResp.getName();
        metadata.parentId = fileMetadataGetResp.getParentId();
        metadata.size = fileMetadataGetResp.getSize();
        metadata.type = fileMetadataGetResp.getType();
        metadata.metadata = fileMetadataGetResp.getMetadata();
        metadata.operations = fileMetadataGetResp.getOperations();
        metadata.subType = fileMetadataGetResp.getSubType();
        metadata.content = CollectionUtils.map(fileMetadataGetResp.getContent(), new Function<FileResp, FileInfo>() {
            @Override
            public FileInfo apply(FileResp fileResp) {
                return FileInfo.from(fileResp);
            }
        }) ;

        return metadata;
    }
}
