package com.lab9.bai2;

import org.openqa.selenium.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BaseTest - Lớp nền tảng cho tất cả Test Class (Bài 2).
 *
 * <ul>
 * <li>ThreadLocal WebDriver — hỗ trợ parallel không xung đột</li>
 * <li>@BeforeMethod nhận browser + env từ testng.xml (@Optional có
 * default)</li>
 * <li>@AfterMethod chụp ảnh khi FAIL →
 * target/screenshots/{testName}_{timestamp}.png</li>
 * </ul>
 */
public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod
    @Parameters({ "browser", "env" })
    public void setUp(@Optional("chrome") String browser, @Optional("dev") String env) {
        String resolvedBrowser = System.getProperty("browser", browser);
        String resolvedEnv = System.getProperty("env", env);

        System.out.printf("══ Thread=%d | Browser=%s | Env=%s ══%n",
            Thread.currentThread().getId(), resolvedBrowser, resolvedEnv);

        WebDriver driver = DriverFactory.createDriver(resolvedBrowser);
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        WebDriver driver = driverThreadLocal.get();
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(driver, result);
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
            driverThreadLocal.remove();
        }
    }

    private void takeScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null)
            return;
        try {
            Path dir = Paths.get("target/screenshots");
            Files.createDirectories(dir);
            String name = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName()
                    + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date())
                    + ".png";
            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Screenshot] → target/screenshots/" + name);
        } catch (IOException | WebDriverException e) {
            System.err.println("[Screenshot] Lỗi: " + e.getMessage());
        }
    }
}
