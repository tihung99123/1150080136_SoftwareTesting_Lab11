package com.lab9.bai5;

import io.qameta.allure.*;
import io.qameta.allure.testng.Tag;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * LoginTest - Bài 5: Kiểm thử đăng nhập SauceDemo với Allure Report.
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
@Feature("Xác thực người dùng")
public class LoginTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    // ─────────────────────────────────────────────────────────
    // TC01 - Đăng nhập thành công
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC01 - Đăng nhập thành công → InventoryPage hiển thị")
    @Story("Đăng nhập thành công")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra rằng người dùng hợp lệ (standard_user) đăng nhập thành công "
               + "và được chuyển đến trang Inventory.")
    public void testLoginSuccess() {
        Allure.step("Đọc dữ liệu test case TC01 từ JSON");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        Allure.step("Mở trang login: https://www.saucedemo.com");
        LoginPage loginPage = new LoginPage(getDriver()).open();

        Allure.step("Nhập username: " + data.get("username"));
        Allure.step("Nhập password");
        InventoryPage inventoryPage = loginPage.login(data.get("username"), data.get("password"));

        Allure.step("Click đăng nhập");

        Allure.step("Xác nhận chuyển trang sang Inventory");
        Assert.assertTrue(
                inventoryPage.isLoaded(),
                "Sau khi login thành công, trang Inventory phải hiển thị");
    }

    // ─────────────────────────────────────────────────────────
    // TC02 - Đăng nhập sai mật khẩu
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC02 - Đăng nhập sai mật khẩu → hiển thị error message")
    @Story("Đăng nhập thất bại - Sai mật khẩu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra rằng khi nhập sai mật khẩu, hệ thống hiển thị thông báo lỗi "
               + "phù hợp và KHÔNG chuyển trang.")
    public void testLoginWrongPassword() {
        Allure.step("Đọc dữ liệu test case TC02 từ JSON");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC02_LoginWrongPassword");

        Allure.step("Mở trang login: https://www.saucedemo.com");
        LoginPage loginPage = new LoginPage(getDriver()).open();

        Allure.step("Nhập username: " + data.get("username"));
        Allure.step("Nhập password sai");
        loginPage.loginExpectingFailure(data.get("username"), data.get("password"));

        Allure.step("Click đăng nhập");

        Allure.step("Xác nhận error message hiển thị trên trang (không chuyển trang)");
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message phải hiển thị");
        Assert.assertTrue(
                loginPage.getErrorMessage().contains(data.get("expectedResult")),
                "Nội dung lỗi không đúng. Actual: " + loginPage.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────
    // TC03 - Tài khoản bị khóa
    // ─────────────────────────────────────────────────────────
    @Test(description = "TC03 - Tài khoản bị locked → hiển thị error message")
    @Story("Đăng nhập thất bại - Tài khoản bị khóa")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra rằng tài khoản locked_out_user bị từ chối đăng nhập "
               + "với thông báo lỗi 'Sorry, this user has been locked out'.")
    public void testLoginLockedUser() {
        Allure.step("Đọc dữ liệu test case TC03 từ JSON");
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC03_LoginLockedUser");

        Allure.step("Mở trang login: https://www.saucedemo.com");
        LoginPage loginPage = new LoginPage(getDriver()).open();

        Allure.step("Nhập username bị khóa: " + data.get("username"));
        Allure.step("Nhập password");
        loginPage.loginExpectingFailure(data.get("username"), data.get("password"));

        Allure.step("Click đăng nhập");

        Allure.step("Xác nhận error message 'locked out' hiển thị");
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message phải hiển thị với locked user");
        Assert.assertTrue(
                loginPage.getErrorMessage().contains(data.get("expectedResult")),
                "Nội dung lỗi không đúng. Actual: " + loginPage.getErrorMessage());
    }
}
