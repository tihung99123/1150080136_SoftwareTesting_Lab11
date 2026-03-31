# Bài 6 — Pipeline Đầy Đủ với Allure Publish lên GitHub Pages

## Mô tả

Bài 6 nâng cấp workflow để **tự động publish Allure Report lên GitHub Pages** sau mỗi lần test chạy thành công trên CI.

Workflow: [`.github/workflows/selenium-full.yml`](../.github/workflows/selenium-full.yml)

---

## Cấu trúc Pipeline

```
selenium-full.yml
├── on: push to main + cron '0 2 * * 1-5' (2AM mỗi ngày trong tuần)
├── job: test (matrix: chrome, firefox)
│   ├── Chạy mvn clean test trên bai5 (Allure-instrumented tests)
│   └── Upload artifact allure-results-{browser}
└── job: publish-report (needs: test, if: always())
    ├── Download allure-results-chrome
    ├── Download allure-results-firefox
    └── simple-elf/allure-report-action → deploy to gh-pages branch
```

---

## Cách kích hoạt GitHub Pages

1. Vào **Settings → Pages** của repository
2. **Source**: Deploy from a branch
3. **Branch**: `gh-pages` → `/ (root)`
4. Nhấn **Save**

Allure Report sẽ xuất hiện tại:  
🌐 **https://tihung99123.github.io/1150080136_SoftwareTesting_Lab11/**

---

## GitHub Secrets cần thiết

Vào **Settings → Secrets and variables → Actions**, thêm:

| Secret name               | Value             |
|---------------------------|-------------------|
| `SAUCEDEMO_USERNAME`      | `standard_user`   |
| `SAUCEDEMO_PASSWORD`      | `secret_sauce`    |

---

## Chạy thủ công

Vào **Actions → Full Selenium CI Pipeline → Run workflow** để chạy pipeline ngay.

---

## Badges trong README

[![Full Selenium CI Pipeline](https://github.com/tihung99123/1150080136_SoftwareTesting_Lab11/actions/workflows/selenium-full.yml/badge.svg)](https://github.com/tihung99123/1150080136_SoftwareTesting_Lab11/actions/workflows/selenium-full.yml)

[![Allure Report](https://img.shields.io/badge/Allure-Report-brightgreen?logo=github)](https://tihung99123.github.io/1150080136_SoftwareTesting_Lab11/)
