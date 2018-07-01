package com.quatrix.api;

import com.quatrix.api.model.FileIds;
import com.quatrix.api.model.FileInfo;
import com.quatrix.api.model.FileMetadata;
import com.quatrix.api.model.FileRenameResult;
import com.quatrix.api.model.Job;
import com.quatrix.api.model.Session;
import com.quatrix.api.model.UploadResult;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface QuatrixApi {

    Session session();

    void login() throws QuatrixApiException;

    void logout() throws QuatrixApiException;

    FileMetadata getHomeDirMeta(boolean includeContent) throws QuatrixApiException;

    FileMetadata getFileMetadata(UUID uuid, boolean includeContent) throws QuatrixApiException;

    FileRenameResult renameFile(UUID uuid, String name, boolean resolveConfilct) throws QuatrixApiException;

    FileIds deleteFile(UUID fileId) throws QuatrixApiException;

    FileInfo createDir(UUID targetDir, String name, boolean resolveConflict) throws QuatrixApiException;

    Job copyFiles(List<UUID> ids, UUID targetDir, boolean resolveConflict) throws QuatrixApiException;

    File download(UUID fileId) throws QuatrixApiException;

    UploadResult upload(File file, UUID parentDir, String name, boolean resolveConflict) throws QuatrixApiException;
}
