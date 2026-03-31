package com.lab9.bai2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>CartPage - Page Object cho trang giỏ hàng SauceDemo</h2>
 *
 * <h3>Đặc biệt:</h3>
 * <ul>
 * <li>{@link #getItemCount()} trả về 0 khi giỏ trống — KHÔNG throw
 * exception</li>
 * <li>{@link #getItemNames()} trả về danh sách rỗng khi giỏ trống</li>
 * </ul>
 *
 * <h3>Fluent Interface:</h3>
 *
 * <pre>{@code
 * int count = cartPage.getItemCount();
 * cartPage.removeFirstItem().getItemCount(); // sau chuỗi remove
 * CheckoutPage checkout = cartPage.goToCheckout();
 * }</pre>
 */
public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.className("cart_item");
    private static final By ITEM_NAME_LABELS = By.className("inventory_item_name");
    private static final By REMOVE_BUTTONS = By.cssSelector("[data-test^='remove']");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CONTINUE_BTN = By.id("continue-shopping");

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
            List<WebElement> removeBtns = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(REMOVE_BUTTONS));
            if (!removeBtns.isEmpty()) {
                int currentCount = getItemCount();
                removeBtns.get(0).click();
                if (currentCount > 0) {
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(ExpectedConditions.numberOfElementsToBe(CART_ITEMS, currentCount - 1));
                }
            }
        } catch (Exception e) {
            System.out.println("[CartPage] removeFirstItem: Giỏ trống, không có item để xóa.");
        }
        return this;
    }

    public CheckoutPage goToCheckout() {
        waitAndClick(CHECKOUT_BUTTON);
        return new CheckoutPage(driver);
    }

    public List<String> getItemNames() {
        try {
            List<WebElement> labels = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAME_LABELS));
            return labels.stream()
                    .filter(WebElement::isDisplayed)
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public InventoryPage continueShopping() {
        waitAndClick(CONTINUE_BTN);
        return new InventoryPage(driver);
    }
}
