package com.quatrix.api.config;

public final class ApiConfigBuilder {

    private String username;
    private String password;
    private String basePath;
    private long keepAliveCallDelay;

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

    public ApiConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ApiConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public ApiConfigBuilder setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public ApiConfigBuilder setKeepAliveCallDelay(long delay) {
        this.keepAliveCallDelay = delay;
        return this;
    }

    public ApiConfig build() {
        return new ApiConfig(this);
    }
}
