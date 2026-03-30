package com.omnitest.platform.core;

import com.omnitest.platform.utils.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.omnitest.platform.db.DBManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

/**
 * Base hooks for all tests: environment logging and {@link TestContext} cleanup.
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void baseSetUp() {
        log.debug("Active env: {}", ConfigManager.getInstance().getEnvironment());
    }

    @AfterMethod(alwaysRun = true)
    public void baseTearDown() {
        TestContext.clear();
    }

    @AfterSuite(alwaysRun = true)
    public void baseAfterSuite() {
        DBManager.close();
    }
}
