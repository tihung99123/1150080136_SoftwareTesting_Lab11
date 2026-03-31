package com.lab9.bai4;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private static final By FIRST_NAME_INPUT = By.id("first-name");
    private static final By LAST_NAME_INPUT = By.id("last-name");
    private static final By ZIP_POSTAL_CODE_INPUT = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage goToCheckoutDirectly() {
        driver.get("https://www.saucedemo.com/checkout-step-one.html");
        waitForPageLoad();
        return this;
    }

    public void fillCheckoutForm(String firstName, String lastName, String postalCode) {
        waitAndType(FIRST_NAME_INPUT, firstName);
        waitAndType(LAST_NAME_INPUT, lastName);
        waitAndType(ZIP_POSTAL_CODE_INPUT, postalCode);
        waitAndClick(CONTINUE_BUTTON);
    }
}