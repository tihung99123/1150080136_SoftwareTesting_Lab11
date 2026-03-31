package com.lab9.bai3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class ConfigReader {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props;

    static {
        props = new Properties();
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null)
                throw new RuntimeException("[ConfigReader] Không tìm thấy: " + CONFIG_FILE);
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("[ConfigReader] Lỗi đọc config: " + e.getMessage(), e);
        }
    }

    private ConfigReader() {
    }

    public static String getBaseUrl() {
        String env = props.getProperty("env", "dev").toLowerCase().trim();
        String url = props.getProperty("url." + env);
        if (url == null || url.isEmpty())
            url = props.getProperty("url.dev", "https://www.saucedemo.com/");
        return url;
    }

    public static String getSecretUsername() {
        return resolveSecret("SAUCEDEMO_USERNAME", "saucedemo.username");
    }

    public static String getSecretPassword() {
        return resolveSecret("SAUCEDEMO_PASSWORD", "saucedemo.password");
    }

    public static String resolveCredential(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
            String key = trimmed.substring(2, trimmed.length() - 1);
            String envValue = System.getenv(key);
            if (envValue != null && !envValue.isBlank()) {
                return envValue;
            }

            String propertyValue = props.getProperty(key.toLowerCase(Locale.ROOT));
            if (propertyValue != null && !propertyValue.isBlank() && !propertyValue.equals(trimmed)) {
                return propertyValue;
            }
            return "";
        }

        return trimmed;
    }

    private static String resolveSecret(String environmentKey, String propertyKey) {
        String envValue = System.getenv(environmentKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String propertyValue = props.getProperty(propertyKey);
        return resolveCredential(propertyValue);
    }
}
