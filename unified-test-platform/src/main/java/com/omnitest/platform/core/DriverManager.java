package com.omnitest.platform.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.omnitest.platform.utils.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * One Playwright stack per thread for parallel TestNG execution.
 */
public final class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private DriverManager() {
    }

    public static Page getPage() {
        Page p = PAGE.get();
        if (p == null) {
            throw new IllegalStateException("Browser not started; call startBrowser() first");
        }
        return p;
    }

    public static boolean hasPage() {
        return PAGE.get() != null;
    }

    public static void startBrowser() {
        if (PAGE.get() != null) {
            return;
        }
        ConfigManager cfg = ConfigManager.getInstance();
        Playwright pw = Playwright.create();
        PLAYWRIGHT.set(pw);
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(cfg.isPlaywrightHeadless());
        String name = cfg.getPlaywrightBrowser().toLowerCase();
        Browser browser = switch (name) {
            case "firefox" -> pw.firefox().launch(launchOptions);
            case "webkit" -> pw.webkit().launch(launchOptions);
            default -> pw.chromium().launch(launchOptions);
        };
        BROWSER.set(browser);
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(cfg.getViewportWidth(), cfg.getViewportHeight()));
        CONTEXT.set(context);
        Page page = context.newPage();
        page.setDefaultTimeout(cfg.getPlaywrightTimeoutMs());
        PAGE.set(page);
        LOG.info("Playwright started for thread {} (browser={})", Thread.currentThread().getName(), name);
    }

    public static void stopBrowser() {
        try {
            Page p = PAGE.get();
            if (p != null) {
                p.close();
            }
        } catch (Exception e) {
            LOG.warn("Error closing page: {}", e.getMessage());
        }
        try {
            BrowserContext ctx = CONTEXT.get();
            if (ctx != null) {
                ctx.close();
            }
        } catch (Exception e) {
            LOG.warn("Error closing context: {}", e.getMessage());
        }
        try {
            Browser b = BROWSER.get();
            if (b != null) {
                b.close();
            }
        } catch (Exception e) {
            LOG.warn("Error closing browser: {}", e.getMessage());
        }
        try {
            Playwright pw = PLAYWRIGHT.get();
            if (pw != null) {
                pw.close();
            }
        } catch (Exception e) {
            LOG.warn("Error closing Playwright: {}", e.getMessage());
        }
        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }

    public static byte[] screenshotPng() {
        if (!hasPage()) {
            return new byte[0];
        }
        return getPage().screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }
}
