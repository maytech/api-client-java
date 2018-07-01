package com.quatrix.api;

import com.quatrix.api.config.ApiConfig;
import com.quatrix.api.model.FileIds;
import com.quatrix.api.model.FileInfo;
import com.quatrix.api.model.FileMetadata;
import com.quatrix.api.model.FileRenameResult;
import com.quatrix.api.model.Job;
import com.quatrix.api.model.Session;
import com.quatrix.api.model.UploadResult;
import com.squareup.okhttp.OkHttpClient;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.CopyMoveFilesReq;
import io.swagger.client.model.FileRenameReq;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.MakeDirReq;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class QuatrixApiImpl implements QuatrixApi {

    private static final long KEEP_ALIVE_DELAY = 5L;

    ScheduledExecutorService keepAliveCallExecutor
            = Executors.newSingleThreadScheduledExecutor();

    /**
     * General API client which makes all real calls to an API. <br />
     */
    private ApiClient apiClient;
    private AuthApi authApi;
    private FileApi fileApi;
    private FileTransferApi fileTransferApi;

    private Session session = null;

    public QuatrixApiImpl(ApiConfig config) {
        this.apiClient = createClient(config);
        this.authApi = new AuthApi(this.apiClient);
        this.fileApi = new FileApi(this.apiClient);
        this.fileTransferApi = new FileTransferApiImpl(this.apiClient);
    }

    public void setKeepAliveCallExecutor(ScheduledExecutorService keepAliveCallExecutor) {
        this.keepAliveCallExecutor = keepAliveCallExecutor;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setAuthApi(AuthApi authApi) {
        this.authApi = authApi;
    }

    public void setFileApi(FileApi fileApi) {
        this.fileApi = fileApi;
    }

    public void setFileTransferApi(FileTransferApi fileTransferApi) {
        this.fileTransferApi = fileTransferApi;
    }

    public Session session() {
        return session;
    }

    @Override
    public void login() throws QuatrixApiException {
        if (session == null) {
            try {
                this.session = Session.from(this.authApi.sessionLoginGet());
                this.apiClient.setApiKey(this.session.getId().toString());

                setupKeepAliveCallback(KEEP_ALIVE_DELAY, TimeUnit.MINUTES);
            } catch (ApiException e) {
                throw new QuatrixApiException(e);
            }
        }
    }

    @Override
    public void logout() throws QuatrixApiException {
        if (session != null) {
            try {
                this.authApi.sessionLogoutGet();
                this.session = null;
            } catch (ApiException e) {
                throw new QuatrixApiException(e);
            } finally {
                keepAliveCallExecutor.shutdownNow();
                this.apiClient.setApiKey(null);
            }
        }
    }

    @Override
    public FileMetadata getHomeDirMeta(boolean includeContent) throws QuatrixApiException {
        final BigDecimal content = includeContent ? BigDecimal.ONE : BigDecimal.ZERO;
        try {
            return FileMetadata.from(this.fileApi.fileMetadataGet(content));
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    @Override
    public FileMetadata getFileMetadata(UUID uuid, boolean includeContent) throws QuatrixApiException {
        final BigDecimal content = includeContent ? BigDecimal.ONE : BigDecimal.ZERO;
        try {
            return FileMetadata.from(this.fileApi.fileMetadataIdGet(uuid, content));
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    @Override
    public FileRenameResult renameFile(UUID uuid, String name, boolean resolveConflict) throws QuatrixApiException {
        final FileRenameReq req = new FileRenameReq()
                .name(name)
                .resolve(resolveConflict);

        try {
            return FileRenameResult.from(this.fileApi.fileRenameIdPost(uuid, req));
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    @Override
    public FileIds deleteFile(UUID fileId) throws QuatrixApiException {
        final IdsReq req = new IdsReq().addIdsItem(fileId);

        try {
            return FileIds.from(this.fileApi.fileDeletePost(req));
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    @Override
    public FileInfo createDir(UUID targetDir, String name, boolean resolveConflict) throws QuatrixApiException {
        final MakeDirReq req = new MakeDirReq()
                .target(targetDir)
                .name(name)
                .resolve(resolveConflict);

        try {
            return FileInfo.from(this.fileApi.fileMakedirPost(req));
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    @Override
    public Job copyFiles(List<UUID> ids, UUID targetDir, boolean resolveConflict) throws QuatrixApiException {
        final CopyMoveFilesReq req = new CopyMoveFilesReq()
                .target(targetDir)
                .ids(ids)
                .resolve(resolveConflict);

        try {
            return Job.from(this.fileApi.fileCopyPost(req));
        } catch (ApiException e) {
            throw new QuatrixApiException(e);
        }
    }

    @Override
    public File download(UUID fileId) throws QuatrixApiException {
        return fileTransferApi.downloadFile(fileId);
    }

    @Override
    public UploadResult upload(File file, UUID parentDir, String name, boolean resolveConflict) throws QuatrixApiException {
        return fileTransferApi.uploadFile(parentDir, file, name, resolveConflict);
    }

    private ApiClient createClient(ApiConfig config) {
        final ApiClient client = new ApiClient();
        if (config.getBasePath() != null) {
            client.setBasePath(config.getBasePath());
        }
        client.setUsername(config.getUsername());
        client.setPassword(config.getPassword());

        return client;
    }

    private void setupKeepAliveCallback(long delay, TimeUnit timeUnit) {
        keepAliveCallExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    authApi.sessionKeepaliveGet();
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }, delay, delay, timeUnit);
    }
}
