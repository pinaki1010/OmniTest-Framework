package com.omnitest.platform.core;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Starts and stops Playwright for UI and end-to-end tests.
 */
public abstract class UiTestBase extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void uiSetUp() {
        DriverManager.startBrowser();
    }

    @AfterMethod(alwaysRun = true)
    public void uiTearDown() {
        DriverManager.stopBrowser();
    }
}
