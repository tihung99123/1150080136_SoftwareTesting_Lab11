package com.lab9.bai5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT  = By.id("user-name");
    private static final By PASSWORD_INPUT  = By.id("password");
    private static final By LOGIN_BUTTON    = By.id("login-button");
    private static final By ERROR_MESSAGE   = By.cssSelector("[data-test='error']");

    private final String baseUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl("dev");
    }

    public LoginPage(WebDriver driver, String env) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl(env);
    }

    public LoginPage open() {
        driver.get(baseUrl);
        waitForPageLoad();
        return this;
    }

    public InventoryPage login(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
        return new InventoryPage(driver);
    }

    public LoginPage loginExpectingFailure(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
        return this;
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }
}
