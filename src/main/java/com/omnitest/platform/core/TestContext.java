package com.omnitest.platform.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-local shared state for cross-layer flows (UI → API → DB).
 */
public final class TestContext {

    private static final ThreadLocal<Map<String, Object>> STORAGE =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    private TestContext() {
    }

    public static void put(String key, Object value) {
        STORAGE.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> get(String key, Class<T> type) {
        Object v = STORAGE.get().get(key);
        if (v == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(v));
    }

    public static Optional<Long> getUserId() {
        return get("userId", Long.class);
    }

    public static void setUserId(long id) {
        put("userId", id);
    }

    public static Optional<String> getEmail() {
        return get("email", String.class);
    }

    public static void setEmail(String email) {
        put("email", email);
    }

    public static Optional<String> getAuthToken() {
        return get("authToken", String.class);
    }

    public static void setAuthToken(String token) {
        put("authToken", token);
    }

    /** Clears all keys for the current thread (call between tests). */
    public static void clear() {
        Map<String, Object> map = STORAGE.get();
        map.clear();
    }

    /** Snapshot for Allure attachments (copy). */
    public static Map<String, Object> snapshot() {
        return new HashMap<>(STORAGE.get());
    }
}
