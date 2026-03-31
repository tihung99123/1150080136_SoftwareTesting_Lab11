package com.lab9.bai4;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        String resolvedBrowser = System.getProperty("browser", browser);
        String gridUrl = System.getProperty("grid.url", "");

        System.out.printf("══ Browser=%s | Grid=%s ══%n", resolvedBrowser, gridUrl.isBlank() ? "local" : gridUrl);

        WebDriver driver = DriverFactory.createDriver(resolvedBrowser, gridUrl);
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driverThreadLocal.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    takeScreenshot(driver, result);
                }
            } finally {
                driver.quit();
                driverThreadLocal.remove();
            }
        }
    }

    private void takeScreenshot(WebDriver driver, ITestResult result) {
        try {
            Path dir = Paths.get(System.getProperty("screenshotDir", "target/screenshots"));
            Files.createDirectories(dir);

            String testName = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName();
            String filename = testName.replaceAll("[\\\\/:*?\"<>|]", "_")
                    + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date()) + ".png";

            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Screenshot] → " + dir.resolve(filename));
        } catch (IOException | WebDriverException e) {
            System.err.println("[Screenshot] Lỗi: " + e.getMessage());
        }
    }
}