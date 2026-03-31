package com.lab9.bai5;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * BaseTest - Lớp nền TestNG cho Bài 5.
 *
 * <ul>
 *   <li>ThreadLocal WebDriver — hỗ trợ parallel không xung đột</li>
 *   <li>@BeforeMethod nhận browser từ testng.xml</li>
 *   <li>Screenshot khi FAIL được xử lý bởi {@link ScreenshotOnFailureListener}
 *       (kết hợp Allure.addAttachment)</li>
 * </ul>
 */
public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        String resolvedBrowser = System.getProperty("browser", browser);
        System.out.printf("══ Thread=%d | Browser=%s ══%n",
                Thread.currentThread().getId(), resolvedBrowser);
        WebDriver driver = DriverFactory.createDriver(resolvedBrowser);
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
