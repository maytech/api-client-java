package com.quatrix.api;

import com.quatrix.api.model.UploadResult;

import java.io.File;
import java.util.UUID;

public interface FileTransferApi {

    UploadResult uploadFile(UUID dirId, File file, String name, boolean resolveConflict) throws QuatrixApiException;

    File downloadFile(UUID fileId) throws QuatrixApiException;
}
