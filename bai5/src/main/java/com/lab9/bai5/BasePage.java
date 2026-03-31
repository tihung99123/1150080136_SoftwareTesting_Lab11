package com.lab9.bai5;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage - Lớp nền tảng cho tất cả Page Object (Bài 5).
 */
public class BasePage {

    protected WebDriver driver;
    private static final int DEFAULT_TIMEOUT = 15;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    private WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public void waitAndClick(By locator) {
        WebElement el = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        el.click();
    }

    public void waitAndType(By locator, String text) {
        WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }

    public String getText(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator))
                .getText().trim();
    }

    public boolean isElementVisible(By locator) {
        try {
            WebElement el = getWait(5).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el.isDisplayed();
        } catch (StaleElementReferenceException e) {
            return isElementVisible(locator);
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForPageLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(wd -> ((JavascriptExecutor) wd)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
