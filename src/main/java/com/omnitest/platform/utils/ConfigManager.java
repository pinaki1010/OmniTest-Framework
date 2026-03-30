package com.omnitest.platform.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Loads {@code config.yaml} from the classpath and resolves the active environment
 * from {@code ENV} or system property {@code env} (defaults to {@code dev}).
 */
public final class ConfigManager {

    private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
    private static final ConfigManager INSTANCE = new ConfigManager();

    private final JsonNode root;
    private final String environment;

    private ConfigManager() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.yaml")) {
            if (in == null) {
                throw new IllegalStateException("config.yaml not found on classpath (src/test/resources)");
            }
            this.root = mapper.readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        String env = System.getenv("ENV");
        if (env == null || env.isBlank()) {
            env = System.getProperty("env", "dev");
        }
        this.environment = env.toLowerCase(Locale.ROOT).trim();
        LOG.info("ConfigManager active environment: {}", this.environment);
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public String getEnvironment() {
        return environment;
    }

    public JsonNode envNode() {
        JsonNode envs = root.path("environments").path(environment);
        if (envs.isMissingNode() || envs.isNull()) {
            throw new IllegalStateException("No environments." + environment + " in config.yaml");
        }
        return envs;
    }

    public String getBaseUrl() {
        return text(envNode(), "baseUrl");
    }

    public String getApiBaseUri() {
        JsonNode api = envNode().path("api");
        String base = text(api, "baseUri");
        String path = textOrDefault(api, "basePath", "");
        if (path.isEmpty()) {
            return base;
        }
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    public String getApiBasePath() {
        return textOrDefault(envNode().path("api"), "basePath", "");
    }

    public String getJdbcUrl() {
        return text(envNode().path("db"), "jdbcUrl");
    }

    public String getDbUsername() {
        return text(envNode().path("db"), "username");
    }

    public String getDbPassword() {
        return text(envNode().path("db"), "password");
    }

    public int getDbPoolSize() {
        return envNode().path("db").path("poolSize").asInt(5);
    }

    public String getDbDriverClassName() {
        return text(envNode().path("db"), "driverClassName");
    }

    public boolean isPlaywrightHeadless() {
        return envNode().path("playwright").path("headless").asBoolean(true);
    }

    public String getPlaywrightBrowser() {
        return textOrDefault(envNode().path("playwright"), "browser", "chromium");
    }

    public int getViewportWidth() {
        return envNode().path("playwright").path("viewportWidth").asInt(1280);
    }

    public int getViewportHeight() {
        return envNode().path("playwright").path("viewportHeight").asInt(720);
    }

    public int getPlaywrightTimeoutMs() {
        return envNode().path("playwright").path("timeoutMs").asInt(30000);
    }

    public String getPactConsumerName() {
        return textOrDefault(root.path("defaults").path("pact"), "consumerName", "omnitest-framework");
    }

    public String getPactProviderName() {
        return textOrDefault(root.path("defaults").path("pact"), "providerName", "user-service");
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull() || !v.isTextual()) {
            throw new IllegalStateException("Missing or invalid string field: " + field);
        }
        return v.asText();
    }

    private static String textOrDefault(JsonNode node, String field, String defaultValue) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull()) {
            return defaultValue;
        }
        return Objects.requireNonNullElse(v.asText(), defaultValue);
    }
}
