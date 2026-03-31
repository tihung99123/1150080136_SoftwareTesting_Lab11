package com.lab9.bai2;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * <h2>CartTest - Kiểm thử giỏ hàng SauceDemo (Bài 2)</h2>
 *
 * <p>
 * Kế thừa BaseTest. Sử dụng chuỗi Fluent Interface đầy đủ:
 * </p>
 *
 * <pre>{@code
 * loginPage.open()
 *         .login(u, p) // → InventoryPage
 *         .addFirstItemToCart() // → InventoryPage
 *         .goToCart() // → CartPage
 *         .getItemCount();
 * }</pre>
 *
 * <p>
 * <b>Quy tắc POM được tuân thủ:</b>
 * </p>
 * <ul>
 * <li>KHÔNG có By.id() hay driver.findElement() trong class này</li>
 * <li>Không dùng Thread.sleep()</li>
 * <li>Dùng getDriver() từ BaseTest</li>
 * </ul>
 */
public class CartTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    @Test(description = "TC04 - Thêm sản phẩm đầu tiên vào giỏ hàng")
    public void testAddFirstItemToCart() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        InventoryPage inventoryPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .addFirstItemToCart();

        Assert.assertEquals(
                inventoryPage.getCartItemCount(), 1,
                "Sau khi add 1 sản phẩm, badge giỏ hàng phải hiển thị 1");
    }

    @Test(description = "TC05 - Thêm sản phẩm theo tên → kiểm tra trong giỏ hàng")
    public void testAddItemByNameAndVerifyInCart() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        String targetItem = "Sauce Labs Bike Light";

        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .addItemByName(targetItem)
                .goToCart();

        List<String> names = cartPage.getItemNames();
        Assert.assertFalse(names.isEmpty(), "Giỏ hàng phải có ít nhất 1 sản phẩm");
        Assert.assertTrue(
                names.contains(targetItem),
                "Sản phẩm '" + targetItem + "' phải có trong giỏ. Actual: " + names);
    }

    @Test(description = "TC06 - Giỏ hàng trống → getItemCount() = 0, không crash")
    public void testCartEmptyReturnsZero() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .goToCart();

        int count = cartPage.getItemCount();
        Assert.assertEquals(count, 0, "Giỏ trống phải trả về 0, không throw exception");

        List<String> names = cartPage.getItemNames();
        Assert.assertTrue(names.isEmpty(), "getItemNames() khi giỏ trống phải trả về list rỗng");
    }

    @Test(description = "TC07 - Add 2 sản phẩm, remove 1 → còn 1 trong giỏ")
    public void testRemoveItemFromCart() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .addFirstItemToCart()
                .addItemByName("Sauce Labs Bike Light")
                .goToCart()
                .removeFirstItem();

        Assert.assertEquals(
                cartPage.getItemCount(), 1,
                "Sau khi remove 1 trong 2 sản phẩm, giỏ phải còn 1");
    }
}
