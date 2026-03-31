package com.lab9.bai5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class InventoryPage extends BasePage {

    private static final By INVENTORY_LIST  = By.className("inventory_list");
    private static final By CART_BADGE      = By.className("shopping_cart_badge");
    private static final By CART_ICON       = By.id("shopping_cart_container");
    private static final By ADD_TO_CART_BTNS = By.cssSelector("[data-test^='add-to-cart']");
    private static final By ITEM_NAMES      = By.className("inventory_item_name");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("inventory") && isElementVisible(INVENTORY_LIST);
    }

    public InventoryPage addFirstItemToCart() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ADD_TO_CART_BTNS))
                .get(0).click();
        return this;
    }

    public InventoryPage addItemByName(String name) {
        By addBtn = By.xpath(
            "//div[contains(@class,'inventory_item')]"
            + "[.//div[contains(@class,'inventory_item_name') and normalize-space(text())='" + name + "']]"
            + "//button[contains(@data-test,'add-to-cart')]"
        );
        waitAndClick(addBtn);
        return this;
    }

    public int getCartItemCount() {
        if (!isElementVisible(CART_BADGE)) return 0;
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
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAMES))
                .stream().map(e -> e.getText().trim()).toList();
    }
}
