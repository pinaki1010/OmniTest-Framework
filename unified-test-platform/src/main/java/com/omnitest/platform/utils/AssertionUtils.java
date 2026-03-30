package com.omnitest.platform.utils;

import org.testng.asserts.SoftAssert;

/** Small wrapper for {@link SoftAssert} when multiple soft checks are needed. */
public final class AssertionUtils {

    private AssertionUtils() {
    }

    public static SoftAssert soft() {
        return new SoftAssert();
    }
}
