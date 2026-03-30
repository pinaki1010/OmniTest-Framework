package com.omnitest.platform.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Opt-in flaky-test retry via {@code @Test(retryAnalyzer = RetryAnalyzer.class)}.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX = 2;
    private final ConcurrentHashMap<String, AtomicInteger> attempts = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        String key = result.getMethod().getQualifiedName();
        AtomicInteger n = attempts.computeIfAbsent(key, k -> new AtomicInteger(0));
        return n.incrementAndGet() <= MAX;
    }
}
