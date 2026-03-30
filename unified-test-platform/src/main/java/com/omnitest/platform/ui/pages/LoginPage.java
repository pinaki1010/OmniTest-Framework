package com.omnitest.platform.ui.pages;

import com.microsoft.playwright.Page;
import com.omnitest.platform.ui.actions.UIActions;
import com.omnitest.platform.utils.ConfigManager;

/**
 * Demo registration page (served by docker demo-ui + WireMock API).
 */
public class LoginPage {

    public static final String REGISTER_PATH = "/register.html";
    public static final String INPUT_NAME = "#name";
    public static final String INPUT_EMAIL = "#email";
    public static final String BTN_SUBMIT = "#submit";
    public static final String LABEL_USER_ID = "#user-id";

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void openRegisterPage() {
        String url = ConfigManager.getInstance().getBaseUrl() + REGISTER_PATH;
        UIActions.navigate(page, url);
    }

    public void registerUser(String name, String email) {
        UIActions.expectVisible(page, INPUT_NAME);
        UIActions.fill(page, INPUT_NAME, name);
        UIActions.fill(page, INPUT_EMAIL, email);
        UIActions.click(page, BTN_SUBMIT);
        UIActions.expectVisible(page, LABEL_USER_ID);
    }

    /** Parses the displayed user id from the demo page. */
    public long readDisplayedUserId() {
        String text = UIActions.textContent(page, LABEL_USER_ID).trim();
        return Long.parseLong(text);
    }
}
