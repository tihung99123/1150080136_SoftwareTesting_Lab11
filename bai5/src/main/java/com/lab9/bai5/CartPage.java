package com.lab9.bai5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CartPage extends BasePage {

    private static final By CART_ITEMS     = By.className("cart_item");
    private static final By ITEM_NAMES     = By.className("inventory_item_name");
    private static final By REMOVE_BUTTONS = By.cssSelector("[data-test^='remove']");
    private static final By CHECKOUT_BTN   = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public int getItemCount() {
        try {
            List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(CART_ITEMS));
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public CartPage removeFirstItem() {
        try {
            List<WebElement> btns = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(REMOVE_BUTTONS));
            if (!btns.isEmpty()) {
                int current = getItemCount();
                btns.get(0).click();
                if (current > 0) {
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(ExpectedConditions.numberOfElementsToBe(CART_ITEMS, current - 1));
                }
            }
        } catch (Exception e) {
            System.out.println("[CartPage] removeFirstItem: Giỏ trống.");
        }
        return this;
    }

    public List<String> getItemNames() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAMES))
                    .stream().map(e -> e.getText().trim()).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public CheckoutPage goToCheckout() {
        waitAndClick(CHECKOUT_BTN);
        return new CheckoutPage(driver);
    }
}
