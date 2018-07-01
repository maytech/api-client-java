package com.quatrix.api;

import com.quatrix.api.config.ApiConfig;
import com.quatrix.api.config.ApiConfigBuilder;
import com.quatrix.api.model.FileIds;
import com.quatrix.api.model.FileInfo;
import com.quatrix.api.model.FileRenameResult;
import com.quatrix.api.model.Job;
import com.quatrix.api.model.Session;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.CopyMoveFilesReq;
import io.swagger.client.model.FileRenameReq;
import io.swagger.client.model.FileRenameResp;
import io.swagger.client.model.FileResp;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.IdsResp;
import io.swagger.client.model.JobResp;
import io.swagger.client.model.MakeDirReq;
import io.swagger.client.model.SessionLoginResp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuatrixApiTest {

    private ApiClient client = Mockito.mock(ApiClient.class);
    private AuthApi authApi = Mockito.mock(AuthApi.class);
    private FileApi fileApi = Mockito.mock(FileApi.class);
    private FileTransferApi fileTransferApi = Mockito.mock(FileTransferApi.class);
    private ScheduledExecutorService executorService = Mockito.mock(ScheduledExecutorService.class);

    private QuatrixApiImpl api;

    @Before
    public void setUp() {
        ApiConfig config = new ApiConfigBuilder()
                .build();

        api = new QuatrixApiImpl(config);
        api.setApiClient(client);
        api.setFileTransferApi(fileTransferApi);
        api.setFileApi(fileApi);
        api.setAuthApi(authApi);
        api.setKeepAliveCallExecutor(executorService);

        Mockito.reset(client, authApi, fileApi, executorService, fileTransferApi);
    }

    @Test
    public void testLogin() throws QuatrixApiException, ApiException {
        final UUID testUuid = UUID.randomUUID();

        Mockito.when(authApi.sessionLoginGet()).thenReturn(new SessionLoginResp().sessionId(testUuid));
        api.login();

        Assert.assertEquals(testUuid, api.session().getId());

        Mockito.verify(executorService).scheduleAtFixedRate(
                Mockito.any(Runnable.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(TimeUnit.class));
    }

    @Test
    public void testCreateDir() throws QuatrixApiException, ApiException {
        final UUID testUuid = UUID.randomUUID();
        final String dirName = "testName";
        MakeDirReq request = new MakeDirReq()
                .target(testUuid)
                .name(dirName)
                .resolve(false);


        Mockito.when(fileApi.fileMakedirPost(request)).thenReturn(new FileResp().id(testUuid).name(dirName));
        FileInfo dirInfo = api.createDir(testUuid, dirName, false);

        Assert.assertEquals(testUuid, dirInfo.getId());
        Assert.assertEquals(dirName, dirInfo.getName());
    }

    @Test
    public void testFileCopy() throws QuatrixApiException, ApiException {
        final UUID testId = UUID.randomUUID();
        final UUID testTarget = UUID.randomUUID();

        List<UUID> uuids = Collections.singletonList(testId);
        final CopyMoveFilesReq req = new CopyMoveFilesReq()
                .ids(uuids)
                .target(testTarget);


        Mockito.when(fileApi.fileCopyPost(req)).thenReturn(new JobResp().jobId(testId));
        Job job = api.copyFiles(uuids, testTarget, true);

        Assert.assertEquals(testId, job.getId());
    }

    @Test
    public void testDeleteFiles() throws QuatrixApiException, ApiException {
        final UUID fileId = UUID.randomUUID();

        final IdsReq req = new IdsReq().addIdsItem(fileId);

        Mockito.when(fileApi.fileDeletePost(req)).thenReturn(new IdsResp().ids(req.getIds()));
        FileIds fileIds = api.deleteFile(fileId);

        Assert.assertEquals(req.getIds(), fileIds.getIds());
    }

    @Test
    public void testRenameFile() throws QuatrixApiException, ApiException {
        final UUID testUuid = UUID.randomUUID();
        final String newName = "test_file";
        FileRenameReq testBody = new FileRenameReq()
                .name(newName)
                .resolve(true);

        Mockito.when(fileApi.fileRenameIdPost(testUuid, testBody)).thenReturn(new FileRenameResp().id(testUuid));
        FileRenameResult result = api.renameFile(testUuid, "test_file", true);

        Assert.assertEquals(testUuid, result.getId());
    }
}
