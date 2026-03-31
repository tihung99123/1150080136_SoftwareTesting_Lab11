package com.lab9.bai2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * <h2>InventoryPage - Page Object cho trang danh sách sản phẩm SauceDemo</h2>
 *
 * <h3>Fluent Interface:</h3>
 *
 * <pre>{@code
 * // Chuỗi hoàn chỉnh từ login đến giỏ hàng:
 * CartPage cart = loginPage.open()
 *         .login("standard_user", "secret_sauce")
 *         .addFirstItemToCart()
 *         .addItemByName("Sauce Labs Bike Light")
 *         .goToCart();
 * }</pre>
 */
public class InventoryPage extends BasePage {

    private static final By INVENTORY_LIST = By.className("inventory_list");
    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By CART_ICON = By.id("shopping_cart_container");
    private static final By ADD_TO_CART_BTNS = By.cssSelector("[data-test^='add-to-cart']");
    private static final By ITEM_NAMES = By.className("inventory_item_name");

    private static final String INVENTORY_URL_FRAGMENT = "inventory";

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        boolean urlOk = driver.getCurrentUrl().contains(INVENTORY_URL_FRAGMENT);
        boolean listOk = isElementVisible(INVENTORY_LIST);
        return urlOk && listOk;
    }

    public InventoryPage addFirstItemToCart() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ADD_TO_CART_BTNS))
                .get(0)
                .click();
        return this;
    }

    public InventoryPage addItemByName(String name) {
        By addBtnByName = By.xpath(
                "//div[contains(@class,'inventory_item')]"
                        + "[.//div[contains(@class,'inventory_item_name') and normalize-space(text())='" + name + "']]"
                        + "//button[contains(@data-test,'add-to-cart')]"
        );
        waitAndClick(addBtnByName);
        return this;
    }

    public int getCartItemCount() {
        if (!isElementVisible(CART_BADGE)) {
            return 0;
        }
        try {
            return Integer.parseInt(getText(CART_BADGE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public CartPage goToCart() {
        waitAndClick(CART_ICON);
        return new CartPage(driver);
    }

    public List<String> getAllItemNames() {
        List<WebElement> elements = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAMES));
        return elements.stream()
                .map(WebElement::getText)
                .collect(java.util.stream.Collectors.toList());
    }
}
