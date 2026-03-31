# BÀI 7 — TEST STRATEGY VÀ TEST PLAN CHO DỰ ÁN SHOPEASY

> **Vai trò**: QA Lead  
> **Dự án**: ShopEasy — Ứng dụng mua sắm online  
> **Tech stack**: Java Spring Boot (API) + React (Web) + React Native (Mobile)  
> **Team**: 4 Developer (2 backend, 2 frontend), 1 QA (bạn), 1 Designer, 1 PM  
> **Sprint**: 2 tuần | Release: Mỗi 4 tuần lên production  
> **Môi trường**: Dev (localhost) → Staging (staging.shopeasy.vn) → Production (shopeasy.vn)  
> **CI/CD**: GitHub Actions đã setup (từ bài 1–6 trong lab này)  
> **Sprint 5 goal**: Ra mắt tính năng _"Thanh toán trả góp qua VPBank"_

---

# PHẦN A — TEST STRATEGY DOCUMENT

---

## 1. Phạm vi kiểm thử (Test Scope)

### 1.1 IN SCOPE — 5 Module kiểm thử

| # | Module | Mô tả chi tiết |
|---|--------|----------------|
| 1 | **Đăng ký / Đăng nhập** | Tạo tài khoản mới, xác thực email OTP, đăng nhập bằng username/password, đăng nhập OAuth (Google, Facebook), chức năng quên mật khẩu, đăng xuất, refresh token |
| 2 | **Tìm kiếm & Lọc sản phẩm** | Tìm kiếm full-text, gợi ý tự động (autocomplete), lọc theo danh mục / khoảng giá / thương hiệu / đánh giá sao, sắp xếp (giá tăng/giảm, mới nhất, bán chạy), phân trang và infinite scroll |
| 3 | **Giỏ hàng** | Thêm/xóa/cập nhật số lượng sản phẩm, áp dụng mã giảm giá (coupon), kiểm tra tồn kho real-time, tính tổng giá trị, lưu giỏ hàng khi chưa đăng nhập (guest cart) |
| 4 | **Thanh toán** | Luồng checkout đầy đủ, tích hợp cổng thanh toán (VNPay, MoMo, COD), **tích hợp VPBank trả góp (Sprint 5)**, xác nhận đơn hàng, xử lý callback từ payment gateway |
| 5 | **Quản lý đơn hàng** | Xem lịch sử đơn hàng, tra cứu trạng thái theo thời gian thực, hủy đơn trước khi giao hàng, yêu cầu hoàn tiền (refund), theo dõi vận chuyển |

### 1.2 OUT SCOPE — 2 Module không kiểm thử trong Sprint này

| # | Module | Lý do OUT SCOPE |
|---|--------|-----------------|
| 1 | **Dashboard Admin / Back-office** | Module quản trị (quản lý sản phẩm, inventory, report doanh thu) thuộc quyền sở hữu của team backend và đã có bộ test riêng chạy trong pipeline nội bộ. Không có user story nào cho Admin dashboard trong Sprint 5 backlog. Module sẽ được đưa vào scope kiểm thử chính thức trong Sprint 6 khi có requirement đầy đủ và dedicated test environment. |
| 2 | **React Native Mobile App (iOS/Android)** | Phiên bản mobile đang trong giai đoạn beta không ổn định — UI thay đổi liên tục giữa các sprint, chưa có device farm hoặc emulator pool được cấu hình trong CI/CD. Việc viết automation test cho mobile hiện tại sẽ gây ra rất nhiều maintenance overhead. Sẽ đưa vào scope khi team mobile hoàn thiện CI pipeline và môi trường test device được setup (dự kiến Sprint 7). |

---

## 2. Phân loại test và tỷ lệ

### 2.1 Tỷ lệ phân bổ

| Loại test | Tỷ lệ | Công cụ | Người thực hiện |
|-----------|-------|---------|-----------------|
| **Unit Test** | 40% | JUnit 5 + Mockito (BE) / Jest + React Testing Library (FE) | Developer (bắt buộc) |
| **API Test** | 25% | RestAssured, Postman/Newman, Spring MockMvc | QA + Developer |
| **UI Test (E2E)** | 20% | Selenium WebDriver + TestNG + Allure Report | QA |
| **Performance Test** | 10% | Apache JMeter, k6 | QA Lead |
| **Security Test** | 5% | OWASP ZAP, Snyk SCA/SAST | QA Lead + DevSecOps |

### 2.2 Giải thích lý do chọn tỷ lệ

**Tại sao Unit Test chiếm 40%?**  
ShopEasy là ứng dụng TMĐT với business logic phức tạp: tính giá sau discount/coupon, commission, thuế VAT, tính toán installment VPBank, kiểm tra điều kiện khuyến mãi. Tất cả logic này nằm ở tầng Service và phải được unit test kỹ để bắt lỗi sớm nhất trong vòng đời phát triển. Unit test chạy nhanh (< 30 giây toàn bộ suite), chi phí maintain thấp, và là nền tảng để refactoring an toàn.

**Tại sao API Test chiếm 25%?**  
ShopEasy sử dụng kiến trúc REST API (Spring Boot) làm nền tảng duy nhất cho cả Web lẫn Mobile. Một bug ở API layer ảnh hưởng đồng thời cả hai platform. API test đảm bảo: contract không bị vỡ khi deploy (schema validation), security headers đúng chuẩn, HTTP status code chính xác, và performance của từng endpoint nằm trong SLA. Đây là layer quan trọng nhất để phát hiện integration bug giữa backend các microservice.

**Tại sao UI Test chỉ 20%?**  
UI test (Selenium) tốn thời gian chạy lớn (5–45 phút cho full regression) và có tỷ lệ flaky cao do phụ thuộc vào DOM, timing, và trạng thái mạng. Với team 1 QA, tập trung UI test vào critical user journey (login → search → add to cart → checkout) và happy path. Các edge case và negative scenario sẽ được cover hiệu quả hơn tại layer API với chi phí thấp hơn nhiều.

**Tại sao Performance Test 10%?**  
Ứng dụng TMĐT có traffic spike rõ rệt trong flash sale, dịp lễ tết, hoặc khi chạy quảng cáo. Cần benchmark đảm bảo response time P95 ≤ 2 giây với 500 concurrent users và throughput ≥ 200 RPS. Tỷ lệ dừng ở 10% vì môi trường staging có tài nguyên hạn chế (không phản ánh đúng production scale) và performance test được lên lịch hàng tuần, không phải sau mỗi commit.

**Tại sao Security Test chỉ 5%?**  
Security test quan trọng về mặt nghiêm trọng nhưng thực hiện theo chu kỳ, không liên tục. Scan tự động (Snyk SCA, OWASP Dependency Check) chạy trong CI/CD pipeline hàng ngày để phát hiện vulnerable dependencies. Full DAST scan (OWASP ZAP) và manual pen test được thực hiện 1 lần trước mỗi production release lớn. Tập trung vào OWASP Top 10: SQL Injection, XSS, CSRF, Broken Access Control, Insecure Direct Object References.

---

## 3. Tiêu chí Definition of Done (DoD)

Một tính năng được coi là **"đã test xong"** và sẵn sàng release khi **đáp ứng TẤT CẢ** các tiêu chí dưới đây. QA Lead chịu trách nhiệm verify và ký off.

### 3.1 Pass Rate yêu cầu

| Loại test | Tiêu chí pass tối thiểu | Ghi chú |
|-----------|------------------------|---------|
| Unit Test | **≥ 95%** pass | Tất cả test case cho critical business logic phải đạt 100% |
| API Test | **100%** pass | Không chấp nhận bất kỳ failure nào ở production endpoints |
| UI Smoke Test | **100%** pass | Smoke suite (≤ 5 phút) bắt buộc xanh trước mỗi release |
| UI Regression | **≥ 90%** pass | Known flaky tests được exclude với documented justification |
| Performance Test | P95 ≤ 2s, error rate ≤ 1% | Đo tại staging với 500 concurrent users, 10 phút sustained load |

### 3.2 Code Coverage yêu cầu

| Layer | Minimum Coverage | Công cụ đo |
|-------|-----------------|------------|
| Service Layer (Business Logic) | **≥ 80%** line coverage | JaCoCo |
| Repository / DAO Layer | **≥ 70%** | JaCoCo |
| Controller Layer | **≥ 75%** | JaCoCo |
| Frontend Components | **≥ 60%** | Istanbul / Jest |

### 3.3 Bug Severity cho phép release

| Severity | Mô tả | Được phép release? |
|----------|-------|--------------------|
| **Critical (S1)** | App crash, mất tiền khách hàng, data corruption, security breach | ❌ **KHÔNG** — Bắt buộc fix trước khi tạo release build |
| **High (S2)** | Core feature bị hỏng hoàn toàn, security vulnerability có thể exploit | ❌ **KHÔNG** — Phải fix hoặc có workaround documented + PM approved |
| **Medium (S3)** | Feature hoạt động nhưng UX tệ, edge case fail, performance chậm | ✅ **CÓ thể** — Phải có JIRA ticket, assign sprint tiếp |
| **Low (S4)** | Lỗi cosmetic, typo, minor UI issue không ảnh hưởng functionality | ✅ **CÓ thể** — Đưa vào backlog |

**Release Gate bắt buộc:**
- 0 (zero) bug S1 và S2 đang mở (open/in-progress)
- PM và QA Lead ký tên vào Test Summary Report
- Smoke test chạy trên staging trong vòng 2 giờ trước release đạt 100%
- Rollback plan đã được documented và reviewed bởi Tech Lead
- Allure Report link đính kèm trong release PR

### 3.4 Checklist bổ sung trước release

- [ ] Test data đã được cleanup — không còn dữ liệu rác trên staging
- [ ] Allure Report được generate và link share trên Slack #qa-reports
- [ ] Regression so với sprint trước không phát sinh bug mới
- [ ] Snyk scan không có dependency vulnerability mới ở mức High/Critical
- [ ] Performance benchmark không tệ hơn sprint trước quá 10%
- [ ] Database migration scripts đã được test trên staging clone

---

## 4. Quản lý rủi ro kiểm thử

### 4.1 Ma trận rủi ro — 4 rủi ro thực tế

| # | Rủi ro | Mô tả | Xác suất | Tác động | Kế hoạch giảm thiểu |
|---|--------|--------|----------|----------|---------------------|
| **R1** | **Tích hợp cổng thanh toán VPBank không ổn định** | API sandbox của VPBank có thể down hoặc trả về kết quả không nhất quán (timeout, wrong status code) trong môi trường test, đặc biệt ngoài giờ hành chính | 🔴 **Cao (60%)** | 🔴 **Nghiêm trọng** — Không thể test được tính năng chính của Sprint 5, làm trễ deadline | **1)** Dùng mock server (WireMock / MockServer) để simulate VPBank API responses khi sandbox down. **2)** Phối hợp với VPBank có dedicated support POC trong sprint. **3)** Đặt lịch test window cố định (10h-16h giờ hành chính) khi sandbox ổn định nhất. **4)** Test với cả 3 scenario: success / failure / timeout. |
| **R2** | **Flaky test trong UI automation làm chậm CI** | Selenium tests không ổn định do race condition (dynamic elements, animation), môi trường CI thiếu tài nguyên (headless chrome bị OOM), hoặc network latency trên staging | 🟡 **Trung bình (45%)** | 🟡 **Trung bình** — Pipeline mất tin cậy, team ignore CI failures, miss real bug | **1)** Implement retry mechanism tối đa 3 lần trong Surefire config. **2)** Thay thế toàn bộ `Thread.sleep()` bằng `WebDriverWait` + `ExpectedConditions`. **3)** Tag flaky tests với `@Flaky` annotation, theo dõi riêng trong Allure. **4)** Chạy UI test trên Selenium Grid (4 nodes) để giảm thời gian và tăng stability. |
| **R3** | **Test data bị nhiễm giữa các test suite** | Dữ liệu test trên staging bị ảnh hưởng bởi các developer khác chạy manual test cùng lúc, gây ra false positive (test pass khi đáng lẽ fail) hoặc false negative (test fail do data bẩn không phải do bug) | 🟡 **Trung bình (40%)** | 🔴 **Cao** — Kết quả test không tin cậy, mất nhiều thời gian debug, có thể bỏ sót bug thực sự | **1)** Mỗi QA và pipeline sử dụng dedicated test account (prefix `qa_auto_`, `qa_ci_`). **2)** Implement `@BeforeMethod` setup và `@AfterMethod` cleanup riêng cho từng test. **3)** Sử dụng database transaction rollback trong integration test (không commit test data). **4)** Snapshot restore database staging vào mỗi tối thứ 2 để bắt đầu tuần mới với clean state. |
| **R4** | **Thiếu nhân lực QA — 1 người cho tính năng phức tạp** | Sprint 5 có tính năng payment integration (VPBank installment) đòi hỏi hiểu sâu về business logic tài chính và cần nhiều test scenario. Chỉ có 1 QA trong team khả năng cao sẽ không cover đủ trong 2 tuần | 🔴 **Cao (70%)** | 🔴 **Cao** — Test coverage thấp, bỏ sót critical bug liên quan đến tiền bạc trước khi release | **1)** Áp dụng risk-based testing: ưu tiên tuyệt đối P1 test cases liên quan đến luồng tiền. **2)** Developer tự chịu trách nhiệm unit test và API test cho code của mình (DoD requirement). **3)** Exploratory testing cho critical path, automation cho regression và sanity check. **4)** Đề xuất PM thuê QA tạm thời (contract) hoặc outsource test session cho payment feature. |

---

## 5. Lịch trình kiểm thử

### 5.1 Bảng lịch chạy các loại test

| Loại test | Tần suất | Trigger | Thời gian chạy | Thông báo kết quả |
|-----------|----------|---------|----------------|-------------------|
| **Smoke Test** | Sau **mỗi commit** vào `main` | GitHub Actions — push event | ~5 phút | Slack `#ci-alerts` — FAIL thì block merge |
| **Regression đầy đủ** | **Hàng đêm** 2:00 AM | GitHub Actions — `cron: '0 2 * * 1-5'` | ~45 phút | Email + Slack `#qa-reports` gửi sáng hôm sau |
| **Performance Test** | **Hàng tuần**, Thứ 7 8:00 AM | GitHub Actions — `cron: '0 8 * * 6'` + Manual | ~2 giờ | Allure Report link gửi cho PM + Tech Lead |
| **Security Scan (Snyk SCA)** | **Hàng ngày** tự động | CI pipeline mỗi commit | ~15 phút | JIRA ticket tự động nếu có vulnerability mới |
| **Security Full Scan (ZAP DAST)** | Trước mỗi **production release** | Manual trigger bởi QA Lead | ~4 giờ | Security report gửi Tech Lead + CTO |
| **Exploratory Test** | **Cuối sprint** (ngày 9–10 / 14) | Thủ công | 1 ngày / sprint | Bug report trong JIRA, daily standup |
| **UAT** | Ngày 13–14 / sprint | PM trigger | 1 ngày | Sign-off form từ PM và Product Owner |

### 5.2 Timeline trong 1 Sprint (2 tuần — 14 ngày làm việc)

```
TUẦN 1 — Development + Test Preparation
  Ngày 1–2   : Sprint planning, phân tích requirement, viết test case, setup test data
  Ngày 3–5   : Dev code, QA viết automation scripts song song, review AC
  ────────────────────────────────────────────────────────────────────────
  Hàng ngày  : Smoke test tự động sau mỗi commit (~5 phút, block on fail)
  02:00 AM   : Regression suite đầy đủ (~45 phút, kết quả vào sáng hôm sau)

TUẦN 2 — Testing + Stabilization
  Ngày 6–8   : API test + Integration test cho VPBank installment
  Ngày 8–9   : UI automation regression, exploratory testing
  Ngày 9–10  : Bug triage meeting, fix verification, re-test
  Ngày 11    : Performance test + Security scan (full sprint report)
  Ngày 12    : Final regression re-run, smoke test trên staging
  Ngày 13–14 : UAT với PM, release sign-off, production deploy
  ────────────────────────────────────────────────────────────────────────
  Thứ 7 sáng : Weekly performance benchmark (JMeter / k6)
  Trước release: OWASP ZAP full scan (~4 giờ) + rollback plan review

DELIVERABLES cuối sprint:
  ✅ Allure Report → Published lên GitHub Pages
  ✅ Test Summary Report → Gửi Slack #releases
  ✅ Bug Report → JIRA với filter Sprint 5
  ✅ Coverage Report → JaCoCo HTML gắn vào release PR
```

---

# PHẦN B — TEST PLAN CHO SPRINT 5

---

## Tính năng Sprint 5: Thanh toán trả góp qua VPBank

> **Mô tả**: Khách hàng có thể chọn thanh toán trả góp lãi suất 0% qua thẻ tín dụng VPBank  
> **Kỳ hạn**: 3 tháng, 6 tháng, 12 tháng  
> **Điều kiện áp dụng**: Giá trị đơn hàng **≥ 3.000.000 VND**  
> **Phí xử lý**: 0% (ShopEasy chịu phí merchant)  
> **API**: VPBank Installment Payment API v2

---

## 6. Phân tích rủi ro nghiệp vụ

### 6.1 Năm kịch bản có thể gây mất tiền người dùng nếu sai

| # | Kịch bản rủi ro | Mô tả chi tiết | Cách phòng tránh |
|---|-----------------|----------------|-----------------|
| **RB1** | **Trừ tiền 2 lần (Double charge)** | Khách nhấn "Xác nhận thanh toán" 2 lần do mạng chậm, hoặc double-click. API VPBank nhận 2 request riêng biệt → tạo 2 transaction khác nhau → thẻ bị debit 2 lần số tiền kỳ đầu | **Idempotency key** bắt buộc trong mỗi request. Disable button ngay khi click lần đầu. Kiểm tra order status = PENDING trước khi gọi payment API. |
| **RB2** | **Tiền bị trừ nhưng đơn hàng không được tạo** | Giao tiếp với VPBank thành công, tiền đã debit từ thẻ, nhưng server ShopEasy bị exception khi INSERT vào database → đơn hàng không tồn tại → khách mất tiền không có đơn hàng | **Two-phase commit pattern**: tạo order với status = PENDING trước, gọi payment, chỉ confirm order sau khi payment success. Implement VPBank webhook reconciliation để phát hiện paid transactions không có order. |
| **RB3** | **Tính sai số tiền trả góp mỗi tháng** | Logic tính toán installment amount bị lỗi rounding cho giá lẻ (ví dụ: 3.100.000 / 3 tháng = 1.033.333.33 → làm tròn sai chiều → tháng cuối thiếu/thừa tiền gây dispute với khách) | **Unit test** cho tất cả combination giá × kỳ hạn (bao gồm giá lẻ). So sánh kết quả với bảng tính chính thức từ VPBank. Quy tắc làm tròn: tháng cuối bù phần lẻ. |
| **RB4** | **Bị charge full price dù chọn trả góp** | Bug trong payment gateway integration: gọi VPBank API thiếu parameter `installment_plan` hoặc `term` → VPBank xử lý như one-time charge → debit toàn bộ giá trị đơn hàng thay vì tháng đầu | **Validate toàn bộ request parameters** trước khi gửi (amount, term, plan_id). Contract testing với mock để verify request schema. Log chi tiết amount + plan_id trước mỗi API call. |
| **RB5** | **Race condition: giá thay đổi trong khi checkout** | Khách đang trên trang checkout với sản phẩm flash sale (giá đặc biệt). Trong lúc process payment, flash sale kết thúc, hệ thống recalculate giá → số tiền trả góp bị thay đổi so với con số khách đã đọc và confirm | **Price locking**: lock giá tại thời điểm "Place Order" vào session / order snapshot. Giá không được cập nhật sau khi user đã ấn confirm. Hiển thị warning nếu giá đã thay đổi và yêu cầu user review lại. |

---

## 7. Thiết kế Test Case — 15 Test Cases

| TC-ID | Tiêu đề | Loại | Ưu tiên | Bước thực hiện tóm tắt | Kết quả mong đợi |
|-------|---------|------|---------|------------------------|-----------------|
| **TC-VP-01** | Hiển thị option trả góp khi đơn hàng ≥ 3 triệu | UI | P1 | 1. Đăng nhập → 2. Thêm sản phẩm giá **3.500.000đ** vào giỏ → 3. Vào trang Checkout → 4. Quan sát mục "Phương thức thanh toán" | Option **"Trả góp 0% — VPBank"** hiển thị với dropdown 3 kỳ hạn: **3 tháng / 6 tháng / 12 tháng** |
| **TC-VP-02** | Ẩn option trả góp khi đơn hàng < 3 triệu | UI | P1 | 1. Đăng nhập → 2. Thêm sản phẩm giá **2.500.000đ** → 3. Checkout → 4. Quan sát Payment section | Option "Trả góp VPBank" **không xuất hiện**. Chỉ hiển thị: VNPay, MoMo, COD |
| **TC-VP-03** | API tính đúng monthly_amount — kỳ hạn 3 tháng, giá chẵn | API | P1 | `POST /api/payment/installment/calculate` với body: `{amount: 3000000, term: 3}` | HTTP 200. Response: `{monthly_amount: 1000000, total: 3000000, interest_rate: 0, term: 3}` |
| **TC-VP-04** | API tính đúng monthly_amount — kỳ hạn 6 tháng, giá lẻ | API | P1 | `POST /api/payment/installment/calculate` với body: `{amount: 3150000, term: 6}` | HTTP 200. Response: `{monthly_amount: 525000, total: 3150000, interest_rate: 0, term: 6}` |
| **TC-VP-05** | API tính đúng monthly_amount — kỳ hạn 12 tháng | API | P1 | `POST /api/payment/installment/calculate` với body: `{amount: 6000000, term: 12}` | HTTP 200. Response: `{monthly_amount: 500000, total: 6000000, interest_rate: 0, term: 12}` |
| **TC-VP-06** | Luồng thanh toán trả góp thành công end-to-end | UI | P1 | 1. Login → 2. Thêm sản phẩm 3.500.000đ → 3. Checkout → 4. Chọn "Trả góp 3 tháng" → 5. Nhập thông tin thẻ VPBank hợp lệ (sandbox) → 6. Nhấn "Xác nhận thanh toán" → 7. Quan sát redirect | Chuyển sang trang **Order Confirmation**. Order status = "PENDING_INSTALLMENT". Email xác nhận gửi đến địa chỉ đã đăng ký trong vòng 2 phút. |
| **TC-VP-07** | Hiển thị lỗi khi thẻ VPBank không đủ hạn mức | UI | P1 | 1. Chọn trả góp 6 tháng → 2. Nhập số thẻ sandbox `insufficient_limit` → 3. Xác nhận | Hiển thị error: **"Thẻ không đủ hạn mức tín dụng. Vui lòng liên hệ VPBank hoặc chọn phương thức khác."** Không trừ tiền. Không tạo order. |
| **TC-VP-08** | API trả lỗi khi term không hợp lệ | API | P2 | `POST /api/payment/installment/calculate` với body: `{amount: 3000000, term: 5}` | HTTP **400 Bad Request**. Body: `{error: "Kỳ hạn không hợp lệ. Chỉ hỗ trợ 3, 6, 12 tháng."}` |
| **TC-VP-09** | API trả lỗi khi amount dưới ngưỡng tối thiểu | API | P2 | `POST /api/payment/installment/calculate` với body: `{amount: 2999999, term: 3}` | HTTP **400 Bad Request**. Body: `{error: "Giá trị đơn tối thiểu cho trả góp là 3.000.000đ."}` |
| **TC-VP-10** | Idempotency — không charge 2 lần với cùng request | API | P1 | Gửi `POST /api/payment/installment` **2 lần liên tiếp** với cùng header `Idempotency-Key: test-key-001` | Lần 1: HTTP 200, transaction tạo mới. Lần 2: HTTP 200, **trả về cùng transaction_id** — không tạo payment mới, không debit thêm |
| **TC-VP-11** | Giá không thay đổi khi flash sale kết thúc trong lúc checkout | UI | P2 | 1. Thêm sản phẩm flash sale 3.200.000đ vào giỏ → 2. Vào Checkout → 3. Simulate flash sale kết thúc (API call thủ công) → 4. Xác nhận thanh toán | Số tiền trả góp hiển thị **vẫn là 3.200.000đ** (giá tại thời điểm add to cart). Không bị recalculate theo giá mới. |
| **TC-VP-12** | Rollback đơn hàng khi VPBank API timeout | API | P2 | Mock VPBank trả về timeout sau 30 giây → Gọi `POST /api/payment/installment` | System retry tối đa **3 lần**. Nếu vẫn fail: HTTP 504, order status = FAILED, **không debit thẻ**. User nhận thông báo: "Thanh toán thất bại. Vui lòng thử lại." |
| **TC-VP-13** | Unit test: tính monthly_amount cho giá lẻ không chia hết | Unit | P1 | Gọi trực tiếp `InstallmentService.calculate(3100000, 3)` trong unit test | Kết quả: tháng 1 + tháng 2 = **1.033.333đ** mỗi tháng, tháng cuối = **1.033.334đ**. Tổng = **3.100.000đ** (không mất, không thừa) |
| **TC-VP-14** | Gửi email xác nhận sau thanh toán trả góp thành công | UI | P2 | Hoàn tất thanh toán trả góp → Kiểm tra inbox email test account | Email nhận trong **vòng 2 phút** với nội dung: tổng tiền đơn, số tiền/tháng, kỳ hạn, **ngày thanh toán kỳ tiếp theo**, mã đơn hàng |
| **TC-VP-15** | Security: thông tin thẻ không lộ trong API response | API | P1 | Thực hiện `POST /api/payment/installment` với thông tin thẻ hợp lệ → Inspect full response body + headers | Response **KHÔNG chứa**: `card_number`, `cvv`, `expiry_date`. Chỉ trả về: `masked_card: "****1234"` và `transaction_id`. Không log thông tin thẻ trong server logs. |

---

## Phụ lục: Môi trường và công cụ

| Môi trường | URL | Mục đích |
|-----------|-----|---------|
| Dev | `localhost:8080` | Developer tự test trong quá trình code |
| Staging | `staging.shopeasy.vn` | QA test, Integration test, UAT |
| VPBank Sandbox | `sandbox-api.vpbank.com.vn` | Test payment integration |
| Production | `shopeasy.vn` | Smoke test sau release, monitoring |

| Công cụ | Mục đích |
|---------|---------|
| Selenium WebDriver 4 + TestNG | UI automation |
| Allure Report 2.x | Test report, published lên GitHub Pages |
| RestAssured | API test |
| WireMock | Mock VPBank sandbox khi offline |
| JMeter / k6 | Performance testing |
| OWASP ZAP | Security DAST |
| Snyk | SCA — kiểm tra dependency vulnerabilities |
| JIRA | Bug tracking và Sprint backlog |
| Slack | Notification từ CI/CD pipeline |
