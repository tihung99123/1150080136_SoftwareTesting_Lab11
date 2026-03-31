package com.lab9.bai4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class JsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<UserData> readUsers(String path) {
        try (InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Không tìm thấy file json: " + path);
            }
            return MAPPER.readValue(inputStream, new TypeReference<List<UserData>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc/parse file JSON: " + e.getMessage(), e);
        }
    }
}