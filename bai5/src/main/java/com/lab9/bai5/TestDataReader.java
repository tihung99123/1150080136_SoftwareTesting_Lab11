package com.lab9.bai5;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TestDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataReader() {}

    public static Map<String, String> getTestCase(String resourcePath, String testCaseName) {
        try (InputStream is = TestDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null)
                throw new RuntimeException("[TestDataReader] Không tìm thấy: " + resourcePath);
            List<Map<String, String>> list = MAPPER.readValue(is, new TypeReference<>() {});
            return list.stream()
                    .filter(m -> testCaseName.equals(m.get("testCase")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("[TestDataReader] Không tìm thấy testCase: " + testCaseName));
        } catch (IOException e) {
            throw new RuntimeException("[TestDataReader] Lỗi đọc JSON: " + e.getMessage(), e);
        }
    }
}
