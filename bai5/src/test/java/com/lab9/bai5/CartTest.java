package com.lab9.bai5;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * CartTest - Bài 5: Kiểm thử giỏ hàng SauceDemo với Allure Report.
 *
 * <p>Mỗi test method có đầy đủ:</p>
 * <ul>
 *   <li>{@code @Feature} - phân loại chức năng</li>
 *   <li>{@code @Story} - user story cụ thể</li>
 *   <li>{@code @Severity} - mức độ nghiêm trọng</li>
 *   <li>{@code @Description} - mô tả chi tiết test case</li>
 *   <li>{@code Allure.step()} - ghi lại từng bước thực hiện</li>
 * </ul>
 */
@Feature("Giỏ hàng")
public class CartTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    // ─────────────────────────────────────────────────────────
    // TC04 - Thêm sản phẩm đầu tiên vào giỏ hàng
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC04 - Thêm sản phẩm đầu tiên vào giỏ hàng → badge = 1")
    @Story("Thêm sản phẩm vào giỏ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra rằng sau khi add sản phẩm đầu tiên, badge giỏ hàng hiển thị số 1.")
    public void testAddFirstItemToCart() {
        Allure.step("Đọc dữ liệu đăng nhập từ JSON (TC01)");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        Allure.step("Mở trang login: https://www.saucedemo.com");
        Allure.step("Nhập username và password hợp lệ");
        Allure.step("Click đăng nhập");
        InventoryPage inventoryPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"));

        Allure.step("Click nút 'Add to cart' cho sản phẩm đầu tiên trong danh sách");
        inventoryPage.addFirstItemToCart();

        Allure.step("Xác nhận badge giỏ hàng hiển thị số 1");
        Assert.assertEquals(
                inventoryPage.getCartItemCount(), 1,
                "Sau khi add 1 sản phẩm, badge giỏ hàng phải hiển thị 1");
    }

    // ─────────────────────────────────────────────────────────
    // TC05 - Thêm sản phẩm theo tên
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC05 - Thêm sản phẩm 'Sauce Labs Bike Light' → kiểm tra trong giỏ")
    @Story("Thêm sản phẩm theo tên")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra rằng sau khi add sản phẩm theo tên, sản phẩm đó xuất hiện trong giỏ hàng.")
    public void testAddItemByNameAndVerifyInCart() {
        Allure.step("Đọc dữ liệu đăng nhập từ JSON (TC01)");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        String targetItem = "Sauce Labs Bike Light";

        Allure.step("Mở trang login: https://www.saucedemo.com");
        Allure.step("Nhập username và password hợp lệ");
        Allure.step("Click đăng nhập");
        InventoryPage inventoryPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"));

        Allure.step("Click 'Add to cart' cho sản phẩm: " + targetItem);
        CartPage cartPage = inventoryPage.addItemByName(targetItem).goToCart();

        Allure.step("Xác nhận sản phẩm '" + targetItem + "' có trong giỏ hàng");
        List<String> names = cartPage.getItemNames();
        Assert.assertFalse(names.isEmpty(), "Giỏ hàng phải có ít nhất 1 sản phẩm");
        Assert.assertTrue(
                names.contains(targetItem),
                "Sản phẩm '" + targetItem + "' phải có trong giỏ. Actual: " + names);
    }

    // ─────────────────────────────────────────────────────────
    // TC06 - Giỏ hàng trống
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC06 - Giỏ hàng trống → getItemCount() = 0 và không crash")
    @Story("Giỏ hàng trống")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra rằng khi giỏ hàng trống, getItemCount() trả về 0 và getItemNames() trả về list rỗng "
               + "— không throw exception.")
    public void testCartEmptyReturnsZero() {
        Allure.step("Đọc dữ liệu đăng nhập từ JSON (TC01)");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        Allure.step("Mở trang login: https://www.saucedemo.com");
        Allure.step("Nhập username và password hợp lệ");
        Allure.step("Click đăng nhập");
        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .goToCart();

        Allure.step("Xác nhận getItemCount() = 0 khi giỏ trống");
        Assert.assertEquals(cartPage.getItemCount(), 0, "Giỏ trống phải trả về 0");

        Allure.step("Xác nhận getItemNames() trả về list rỗng khi giỏ trống");
        Assert.assertTrue(cartPage.getItemNames().isEmpty(), "getItemNames() khi giỏ trống phải rỗng");
    }

    // ─────────────────────────────────────────────────────────
    // TC07 - Add 2 sản phẩm rồi xóa 1
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC07 - Add 2 sản phẩm, remove 1 → giỏ còn 1 sản phẩm")
    @Story("Xóa sản phẩm khỏi giỏ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tính năng xóa sản phẩm: sau khi add 2 sản phẩm và remove 1, "
               + "giỏ hàng phải còn đúng 1 sản phẩm.")
    public void testRemoveItemFromCart() {
        Allure.step("Đọc dữ liệu đăng nhập từ JSON (TC01)");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        Allure.step("Mở trang login: https://www.saucedemo.com");
        Allure.step("Nhập username và password hợp lệ");
        Allure.step("Click đăng nhập");
        InventoryPage inventoryPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"));

        Allure.step("Thêm sản phẩm đầu tiên vào giỏ");
        Allure.step("Thêm sản phẩm 'Sauce Labs Bike Light' vào giỏ");
        CartPage cartPage = inventoryPage
                .addFirstItemToCart()
                .addItemByName("Sauce Labs Bike Light")
                .goToCart();

        Allure.step("Click Remove cho sản phẩm đầu tiên trong giỏ");
        cartPage.removeFirstItem();

        Allure.step("Xác nhận giỏ hàng còn đúng 1 sản phẩm");
        Assert.assertEquals(
                cartPage.getItemCount(), 1,
                "Sau khi remove 1 trong 2 sản phẩm, giỏ phải còn 1");
    }
}
