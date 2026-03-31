package com.lab9.bai5;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * ScreenshotOnFailureListener - ITestListener cho Allure Report (Bài 5).
 *
 * <p>Khi test FAIL, listener này sẽ:</p>
 * <ol>
 *   <li>Lấy WebDriver từ instance của BaseTest</li>
 *   <li>Chụp ảnh màn hình dưới dạng byte[]</li>
 *   <li>Đính kèm vào Allure Report qua {@code Allure.addAttachment()}</li>
 * </ol>
 *
 * <p>Được đăng ký trong testng.xml:</p>
 * <pre>{@code
 * <listeners>
 *     <listener class-name="com.lab9.bai5.ScreenshotOnFailureListener"/>
 * </listeners>
 * }</pre>
 */
public class ScreenshotOnFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest)) return;

        WebDriver driver = ((BaseTest) instance).getDriver();
        if (!(driver instanceof TakesScreenshot)) return;

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String attachmentName = "Screenshot - " + result.getTestClass().getRealClass().getSimpleName()
                    + "." + result.getMethod().getMethodName();
            Allure.addAttachment(
                    attachmentName,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
            System.out.println("[ScreenshotListener] Đã đính kèm ảnh vào Allure: " + attachmentName);
        } catch (Exception e) {
            System.err.println("[ScreenshotListener] Lỗi chụp ảnh: " + e.getMessage());
        }
    }
}
