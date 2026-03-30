package com.omnitest.platform.reporting;

import com.omnitest.platform.core.DriverManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * Captures a Playwright screenshot on failure and attaches it to Allure.
 */
public class TestFailureScreenshotListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestFailureScreenshotListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        if (!DriverManager.hasPage()) {
            return;
        }
        try {
            byte[] png = DriverManager.screenshotPng();
            if (png.length > 0) {
                Allure.addAttachment("ui-failure-screenshot", "image/png",
                        new ByteArrayInputStream(png), ".png");
            }
        } catch (Exception e) {
            LOG.warn("Could not capture failure screenshot: {}", e.getMessage());
        }
    }
}
