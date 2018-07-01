package com.quatrix.api;

import com.google.gson.Gson;
import com.quatrix.api.config.ApiConfigBuilder;
import com.quatrix.api.model.FileIds;
import com.quatrix.api.model.FileInfo;
import com.quatrix.api.model.FileRenameResult;
import com.quatrix.api.model.Job;
import com.quatrix.api.model.Session;
import io.swagger.client.ApiClient;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.CopyMoveFilesReq;
import io.swagger.client.model.FileRenameResp;
import io.swagger.client.model.FileResp;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.JobResp;
import io.swagger.client.model.MakeDirReq;
import io.swagger.client.model.SessionLoginResp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class QuatrixApiMockTest {

    private ApiClient apiClient = new ApiClient();
    private AuthApi authApi = new AuthApi();
    private FileApi fileApi = new FileApi();
    private QuatrixApiImpl api = new QuatrixApiImpl(
        new ApiConfigBuilder()
            .setBasePath("http://localhost:9025")
            .build()
    );
    private Gson gson = new Gson();
    private MockServerClient mockServerClient;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 9025);

    @Before
    public void init() {
        apiClient.setBasePath("http://localhost:9000");
        apiClient.setUsername("alexeykrynka@gmail.com");
        apiClient.setPassword("Quatrix_Connector");

        fileApi.setApiClient(apiClient);
        authApi.setApiClient(apiClient);

        mockServerClient.reset();
    }

    @Test
    public void testLoginSuccessful() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("Content-type", "application/json"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson.toJson(new SessionLoginResp()
                                .sessionId(testUuid))));

        api.login();
        assertEquals(testUuid, api.session().getId());
    }

    @Test
    public void testLoginMFARequired() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        apiClient.setUsername(null);
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("Content-type", "application/json"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(207)
                        .withBody(gson.toJson(new SessionLoginResp()
                                .sessionId(testUuid))));

        api.login();
        assertEquals(testUuid, api.session().getId());
    }

    @Test(expected = QuatrixApiException.class)
    public void testLoginUnauthorized() throws QuatrixApiException {
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("Content-type", "application/json"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        api.login();
    }

    @Test
    public void testRenameFileSuccessful() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();

        mockServerClient.when(HttpRequest
                .request("/file/rename/" + testUuid)
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson.toJson(new FileRenameResp().id(testUuid))));

        FileRenameResult result = api.renameFile(testUuid, "test", false);
        assertEquals(testUuid, result.getId());
    }

    @Test(expected = QuatrixApiException.class)
    public void testRenameFileBadRequest() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();

        mockServerClient.when(HttpRequest
                .request("/file/rename/" + testUuid)
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400));

        api.renameFile(testUuid, "test", false);
    }

    @Test(expected = QuatrixApiException.class)
    public void testRenameFileUnauthorized() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/file/rename/" + testUuid)
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        api.renameFile(testUuid, "test", false);
    }

    @Test
    public void testDeleteFilesSuccessful() throws QuatrixApiException {
        IdsReq req = new IdsReq();

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson.toJson(req)));

        FileIds fileIds = api.deleteFile(UUID.randomUUID());
        assertEquals(req.getIds(), fileIds.getIds());
    }

    @Test
    public void testDeleteFilesMoreThen10Successful() throws QuatrixApiException {
        IdsReq req = new IdsReq().ids(insertIds());

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(202)
                        .withBody(gson.toJson(req)));

        FileIds fileIds = api.deleteFile(UUID.randomUUID());
        assertEquals(req.getIds(), fileIds.getIds());
    }

    @Test(expected = QuatrixApiException.class)
    public void testDeleteFilesBadRequest() throws QuatrixApiException {
        IdsReq req = new IdsReq();

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400));

        api.deleteFile(UUID.randomUUID());
    }

    @Test(expected = QuatrixApiException.class)
    public void testDeleteFilesUnauthorized() throws QuatrixApiException {
        IdsReq req = new IdsReq();
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        api.deleteFile(UUID.randomUUID());
    }

    @Test
    public void testMakeDirSuccessful() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        MakeDirReq req = new MakeDirReq()
                .target(testUuid)
                .name("test")
                .resolve(true);

        mockServerClient.when(HttpRequest
                .request("/file/makedir")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(201)
                        .withBody(gson.toJson(new FileResp().id(testUuid))));

        FileInfo response = api.createDir(testUuid, "test", true);
        assertEquals(testUuid, response.getId());
    }

    @Test(expected = QuatrixApiException.class)
    public void testMakeDirBadRequest() throws QuatrixApiException {
        MakeDirReq req = new MakeDirReq();

        mockServerClient.when(HttpRequest
                .request("/file/makedir")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400)
                );

        api.createDir(UUID.randomUUID(), "test", false);
    }

    @Test(expected = QuatrixApiException.class)
    public void testMakeDirUnauthorized() throws QuatrixApiException {
        MakeDirReq req = new MakeDirReq();

        mockServerClient.when(HttpRequest
                .request("/file/makedir")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401)
                );

        api.createDir(UUID.randomUUID(), "test", false);
    }

    @Test
    public void testFileCopySuccessful() throws QuatrixApiException {
        final UUID testId = UUID.randomUUID();
        final UUID fileToCopy = UUID.randomUUID();
        CopyMoveFilesReq req = new CopyMoveFilesReq()
                .target(testId)
                .addIdsItem(fileToCopy)
                .resolve(true);

        mockServerClient.when(HttpRequest
                .request("/file/copy")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(202)
                        .withBody(gson.toJson(new JobResp().jobId(testId))));

        Job response = api.copyFiles(Arrays.asList(fileToCopy), testId, true);

        assertEquals(testId, response.getId());
    }

    @Test(expected = QuatrixApiException.class)
    public void testFileCopyBadRequest() throws QuatrixApiException {
        CopyMoveFilesReq req = new CopyMoveFilesReq();

        mockServerClient.when(HttpRequest
                .request("/file/copy")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400));

        api.copyFiles(Arrays.asList(UUID.randomUUID()), UUID.randomUUID(), false);
    }

    @Test(expected = QuatrixApiException.class)
    public void testFileCopyUnauthorized() throws QuatrixApiException {
        CopyMoveFilesReq req = new CopyMoveFilesReq();

        mockServerClient.when(HttpRequest
                .request("/file/copy")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        api.copyFiles(Arrays.asList(UUID.randomUUID()), UUID.randomUUID(), false);
    }

    private List<UUID> insertIds() {
        List<UUID> listFilesForDelete = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            listFilesForDelete.add(UUID.randomUUID());
        }

        return listFilesForDelete;
    }
}

