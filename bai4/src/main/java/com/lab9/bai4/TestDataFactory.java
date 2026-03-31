package com.lab9.bai4;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.Map;

public class TestDataFactory {

    private static final Faker faker = new Faker(new Locale("en"));

    public static Map<String, String> randomCheckoutData() {
        return Map.of(
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "postalCode", faker.address().zipCode());
    }
}