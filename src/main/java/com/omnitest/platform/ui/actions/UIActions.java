package com.omnitest.platform.ui.actions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Reusable UI helpers with Playwright auto-waits.
 */
public final class UIActions {

    private UIActions() {
    }

    public static void navigate(Page page, String url) {
        page.navigate(url);
    }

    public static void fill(Page page, String selector, String value) {
        page.locator(selector).fill(value);
    }

    public static void click(Page page, String selector) {
        page.locator(selector).click();
    }

    public static void expectVisible(Page page, String selector) {
        page.locator(selector).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    public static String textContent(Page page, String selector) {
        return page.locator(selector).innerText();
    }
}
