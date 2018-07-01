package com.quatrix.api.config;

public final class ApiConfig {

    private final String username;
    private final String password;
    private final String basePath;
    private final long keepAliveCallDelay;

    ApiConfig(ApiConfigBuilder builder) {
        this.username = builder.getUsername();
        this.password = builder.getPassword();
        this.basePath = builder.getBasePath();
        this.keepAliveCallDelay = builder.getKeepAliveCallDelay();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBasePath() {
        return basePath;
    }

    public long getKeepAliveCallDelay() {
        return keepAliveCallDelay;
    }
}
