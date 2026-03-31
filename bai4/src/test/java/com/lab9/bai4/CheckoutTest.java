package com.lab9.bai4;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class CheckoutTest extends BaseTest {

    @Test(invocationCount = 2, description = "Test Data-driven ngẫu nhiên qua JavaFaker")
    public void testCheckoutWithRandomData() {
        Map<String, String> formData = TestDataFactory.randomCheckoutData();

        System.out.println("\n============ KẾT QUẢ DATA FAKER ============");
        System.out.println("First Name : " + formData.get("firstName"));
        System.out.println("Last Name  : " + formData.get("lastName"));
        System.out.println("Postal Code: " + formData.get("postalCode"));
        System.out.println("============================================\n");

        new LoginPage(getDriver())
                .open()
                .login("standard_user", "secret_sauce");

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToCheckoutDirectly();
        checkoutPage.fillCheckoutForm(
                formData.get("firstName"),
                formData.get("lastName"),
                formData.get("postalCode"));

        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-two"),
                "Lỗi: Không thể điền dữ liệu Faker qua Next Step");
    }
}