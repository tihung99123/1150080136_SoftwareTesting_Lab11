package com.lab9.bai4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver(String browser, String gridUrl) {
        String normalizedBrowser = browser == null ? "chrome" : browser.toLowerCase(Locale.ROOT);
        String resolvedGridUrl = gridUrl == null ? "" : gridUrl.trim();

        if (!resolvedGridUrl.isEmpty()) {
            return createRemoteDriver(normalizedBrowser, resolvedGridUrl);
        }

        if ("firefox".equals(normalizedBrowser)) {
            return createLocalFirefoxDriver();
        }
        return createLocalChromeDriver();
    }

    private static WebDriver createLocalChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = baseChromeOptions();
        return new ChromeDriver(options);
    }

    private static WebDriver createLocalFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = baseFirefoxOptions();
        return new FirefoxDriver(options);
    }

    private static WebDriver createRemoteDriver(String browser, String gridUrl) {
        try {
            if ("firefox".equals(browser)) {
                return new RemoteWebDriver(new URL(gridUrl), baseFirefoxOptions());
            }
            return new RemoteWebDriver(new URL(gridUrl), baseChromeOptions());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid grid.url: " + gridUrl, e);
        }
    }

    private static ChromeOptions baseChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private static FirefoxOptions baseFirefoxOptions() {
        return new FirefoxOptions();
    }
}