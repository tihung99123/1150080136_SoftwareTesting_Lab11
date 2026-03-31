package com.lab9.bai3;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

import org.testng.annotations.Test;

public class CreateExcelForOtherBais {

    private static final String USERNAME_PLACEHOLDER = "${SAUCEDEMO_USERNAME}";
    private static final String PASSWORD_PLACEHOLDER = "${SAUCEDEMO_PASSWORD}";

    @Test
    public void generateExcelForAllFiles() throws Exception {
        // Bai 1 & 2 json data
        createExcel("bai1/src/test/resources/testdata/login_data.xlsx");
        createExcel("bai2/src/test/resources/testdata/login_data.xlsx");

        // Bai 4 users.json
        createExcelBai4("bai4/src/test/resources/testdata/users.xlsx");

        System.out.println("DONE GENERATING EXCEL FILES FOR BAI 1, 2, 4");
    }

    private static void createExcel(String path) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("login_data");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("testCase");
        header.createCell(1).setCellValue("username");
        header.createCell(2).setCellValue("password");
        header.createCell(3).setCellValue("expectedResult");

        String[][] data = {
            { "TC01_LoginSuccess", USERNAME_PLACEHOLDER, PASSWORD_PLACEHOLDER, "login_success" },
            { "TC02_LoginWrongPassword", USERNAME_PLACEHOLDER, "wrong_password", "Username and password do not match" },
            { "TC03_LoginLockedUser", "locked_out_user", PASSWORD_PLACEHOLDER, "Sorry, this user has been locked out" }
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                row.createCell(j).setCellValue(data[i][j]);
            }
        }

        File f = new File(path);
        f.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            wb.write(fos);
        }
        wb.close();
    }

    private static void createExcelBai4(String path) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("users");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("username");
        header.createCell(1).setCellValue("password");
        header.createCell(2).setCellValue("role");
        header.createCell(3).setCellValue("expectSuccess");
        header.createCell(4).setCellValue("description");

        Object[][] data = {
            { USERNAME_PLACEHOLDER, PASSWORD_PLACEHOLDER, "admin", true, "JSON01 - Dang nhap thanh cong voi user chuan" },
            { "locked_out_user", PASSWORD_PLACEHOLDER, "guest", false, "JSON02 - That bai do User bi lock" },
                { USERNAME_PLACEHOLDER, "sai_password", "guest", false, "JSON03 - That bai do nhap sai password" },
            { "", PASSWORD_PLACEHOLDER, "guest", false, "JSON04 - That bai vi chua nhap user name" },
            { "problem_user", PASSWORD_PLACEHOLDER, "user", true, "JSON05 - Dang nhap thanh cong voi user hay gay loi" }
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue((String) data[i][0]);
            row.createCell(1).setCellValue((String) data[i][1]);
            row.createCell(2).setCellValue((String) data[i][2]);
            row.createCell(3).setCellValue((Boolean) data[i][3]);
            row.createCell(4).setCellValue((String) data[i][4]);
        }

        File f = new File(path);
        f.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            wb.write(fos);
        }
        wb.close();
    }
}
