package com.omnitest.platform.reporting;

import io.qameta.allure.Allure;

import java.util.Map;

/** Helpers for Allure steps and attachments (DB rows, contract summaries). */
public final class AllureManager {

    private AllureManager() {
    }

    public static void attachText(String name, String content) {
        if (content == null) {
            content = "";
        }
        Allure.addAttachment(name, "text/plain", content, ".txt");
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", json, ".json");
    }

    public static void attachContextSnapshot(Map<String, Object> snapshot) {
        StringBuilder sb = new StringBuilder();
        snapshot.forEach((k, v) -> sb.append(k).append("=").append(v).append('\n'));
        attachText("test-context", sb.toString());
    }

    public static void step(String name, Runnable runnable) {
        Allure.step(name, () -> {
            runnable.run();
            return null;
        });
    }
}
