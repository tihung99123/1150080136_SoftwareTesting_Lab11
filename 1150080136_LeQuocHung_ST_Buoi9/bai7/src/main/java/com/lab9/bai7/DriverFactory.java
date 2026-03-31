package com.lab9.bai7;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Locale;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver(String browser) {
        String normalizedBrowser = browser == null ? "chrome" : browser.toLowerCase(Locale.ROOT);
        if ("firefox".equals(normalizedBrowser)) {
            return createFirefoxDriver();
        }
        return createChromeDriver();
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        if (isCi()) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        if (isCi()) {
            options.addArguments("--headless");
        }
        return new FirefoxDriver(options);
    }

    private static boolean isCi() {
        return Boolean.parseBoolean(System.getenv().getOrDefault("CI", "false"));
    }
}
