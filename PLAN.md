# Kế hoạch thay đổi code

## Quy định bắt buộc cho AI

- Đọc file này trước khi bắt đầu thay đổi code.
- Mọi thay đổi có ảnh hưởng đến logic, hành vi, UI, dữ liệu, build hoặc cấu hình phải được ghi thành một công việc trong file này trước khi sửa file liên quan.
- Cập nhật `Trạng thái`, `Ngày thực hiện`, `Ngày hoàn thành`, file ảnh hưởng và tính năng có thể bị ảnh hưởng trong suốt quá trình làm việc.
- Không xóa lịch sử công việc đã hoàn thành hoặc đã từ bỏ.

## Trạng thái hợp lệ

- `chưa làm`
- `đang làm`
- `tạm hoãn`
- `hoàn thành`
- `từ bỏ`

## Mẫu công việc

### [Tên công việc]

- **Ngày khởi tạo:** YYYY-MM-DD
- **Ngày thực hiện:** YYYY-MM-DD hoặc `-`
- **Ngày hoàn thành:** YYYY-MM-DD hoặc `-`
- **Trạng thái:** `chưa làm`
- **Công việc chi tiết cần thực hiện:**
  - ...
- **Unit test cần viết/chạy:**
  - Test cần bổ sung: ...
  - Lệnh kiểm tra: `./gradlew.bat :app:testDebugUnitTest`
- **Các file ảnh hưởng:**
  - ...
- **Các tính năng có thể bị ảnh hưởng:**
  - ...

## Lịch sử công việc

### Nâng target SDK lên 37 và tích hợp Hilt

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Nâng `compileSdk` và `targetSdk` của module `:app` từ 36 lên 37.
  - Khai báo Hilt và KSP trong version catalog, áp dụng plugin và dependency cho `:app`.
  - Tạo `Application` khởi tạo Hilt, đăng ký trong manifest và bật injection cho `MainActivity`.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test logic thuần Kotlin; đây là thay đổi build/runtime wiring.
  - `:app:testDebugUnitTest` — đạt.
  - `:app:assembleDebug` — đạt.
  - Không chạy được `:app:connectedDebugAndroidTest` vì ADB server không khởi động được và không có emulator/device khả dụng.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `build.gradle.kts`
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/java/com/sabo/test/CodeBaseApplication.kt`
  - `app/src/main/java/com/sabo/test/MainActivity.kt`
- **Các tính năng có thể bị ảnh hưởng:**
  - Biên dịch/target hành vi Android API 37.
  - Khởi tạo dependency injection bằng Hilt cho application và activity.

### Thiết lập quy trình PLAN cho AI

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Tạo file kế hoạch dùng chung cho AI trước mỗi lần thay đổi project.
  - Quy định trạng thái và các trường thông tin bắt buộc cho từng công việc.
  - Cập nhật skill Android để AI đọc và cập nhật file này trước khi sửa code.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - Skill người dùng `codebasev2-android/SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Không ảnh hưởng hành vi runtime của ứng dụng.
  - Bổ sung quy trình quản lý thay đổi trước khi code.

### Bổ sung yêu cầu unit test

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Ghi rõ unit test cần viết và lệnh test trong từng công việc thay đổi code.
  - Bắt buộc thêm hoặc cập nhật unit test cho logic thuần Kotlin.
  - Không tạo test giả chỉ để tăng số lượng; nếu chưa có logic nghiệp vụ thì ghi rõ chưa phát sinh test.
- **Unit test cần viết/chạy:**
  - Test hiện có: `app/src/test/java/com/sabo/test/ExampleUnitTest.kt` chỉ là test mẫu.
  - Chưa thêm test mới vì project hiện chưa có logic nghiệp vụ riêng để kiểm thử có ý nghĩa.
  - Khi có logic mới, chạy `./gradlew.bat :app:testDebugUnitTest`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - Skill người dùng `codebasev2-android/SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Không ảnh hưởng hành vi runtime của ứng dụng.
  - Bổ sung yêu cầu kiểm thử hồi quy cho các thay đổi logic.

### Tạo AGENTS.md cho hướng dẫn AI của project

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Tạo `AGENTS.md` ở thư mục gốc để AI đọc trước khi code.
  - Quy định AI phải đọc và cập nhật `PLAN.md` trước các thay đổi logic, UI, dữ liệu, build hoặc cấu hình.
  - Quy định unit test phải đi kèm các thay đổi logic thuần Kotlin.
  - Ghi lại quy ước Android và CodeGraph của project.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test vì chỉ thêm file hướng dẫn.
- **Các file ảnh hưởng:**
  - `AGENTS.md`
  - `PLAN.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Không ảnh hưởng hành vi runtime của ứng dụng.
  - Ảnh hưởng quy trình AI trước và trong khi sửa code.
