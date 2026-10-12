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

### Đổi coordinate JitPack của module core

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Đổi publication của `:core` từ artifact `core` sang artifact `CodeBaseV2`.
  - Giữ group là `com.github.vtabk2` để dependency dùng được dạng `com.github.vtabk2:CodeBaseV2:1.0.0`.
  - Cập nhật README và convention JitPack.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test logic thuần Kotlin; chạy publish release tới Maven Local để kiểm tra coordinate.
  - `:core:publishReleasePublicationToMavenLocal` — đạt.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `build.gradle.kts`
  - `README.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Coordinate dependency JitPack của thư viện cha `core`.

### Thêm module core và hai module con cho JitPack

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Đăng ký `:core`, `:core:ads` và `:core:baseui` trong Gradle settings.
  - Cấu hình `:core` là Android Library cha, kéo theo `ads` và `baseui` bằng dependency `api`.
  - Cấu hình ba module Android Library xuất bản release AAR bằng `maven-publish` cho JitPack.
  - Để `:app` dùng `project(":core")` thay vì gọi trực tiếp module con.
  - Chuyển `CodeBaseApplication` sang `:core:ads` theo yêu cầu.
  - Vì Hilt không cho `@HiltAndroidApp` trong Android Library, giữ `CodeBaseApplication` làm base class và tạo `AppApplication` Hilt root trong `:app`.
  - Đặt group/version theo repository GitHub và thêm cấu hình JDK 17 cho môi trường JitPack.
  - Ghi cách dùng dependency JitPack vào README và cập nhật tài liệu cấu trúc module.
  - Đối chiếu convention publishing/JitPack với repo mẫu `D:\Android\CodeTA\CodeBase`.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test logic thuần Kotlin; thay đổi chỉ là module/build/publishing wiring.
  - `:core:ads:assembleRelease`, `:core:baseui:assembleRelease`, `:core:assembleRelease`, `:app:assembleDebug` — đạt.
  - `publishReleasePublicationToMavenLocal` cho cả ba module — đạt.
  - Không chạy connected test vì môi trường không có ADB/emulator khả dụng.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `AGENTS.md`
  - `settings.gradle.kts`
  - `build.gradle.kts`
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `app/src/main/java/com/sabo/test/CodeBaseApplication.kt`
  - `app/src/main/java/com/sabo/test/AppApplication.kt`
  - `core/build.gradle.kts`
  - `core/ads/build.gradle.kts`
  - `core/ads/src/main/java/com/core/ads/CodeBaseApplication.kt`
  - `core/baseui/build.gradle.kts`
  - `jitpack.yml`
  - `README.md`
  - `.gitignore`
- **Các tính năng có thể bị ảnh hưởng:**
  - Build/publishing AAR của `core`, `ads` và `baseui`.
  - Ứng dụng mẫu sử dụng dependency cha `com.github.vtabk2.CodeBaseV2:core:1.0.0`.

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

### Chuẩn hóa ViewBinding, KSP và Hilt cho skill Android

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Bổ sung vào hướng dẫn project rằng UI XML phải ưu tiên ViewBinding.
  - Bổ sung quy ước dùng KSP cho code generation/annotation processing, không dùng `kapt` nếu không có yêu cầu đặc biệt.
  - Bổ sung quy ước dùng Hilt cho dependency injection và khai báo compiler qua KSP.
  - Đồng bộ các quy ước trên vào skill `codebasev2-android`.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test logic thuần Kotlin; chỉ thay đổi tài liệu/quy ước của agent.
  - Kiểm tra thủ công `git diff` và `git status --short`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `AGENTS.md`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Quy trình triển khai UI XML, annotation processing và dependency injection trong các thay đổi Android sau này.

### Chuẩn hóa Java 17 cho project và skill Android

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Cập nhật tài liệu project và skill Android dùng Java source/target 17.
  - Đối chiếu cấu hình Gradle của `:app`, `:core`, `:core:ads` và `:core:baseui`.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test logic thuần Kotlin; chạy kiểm tra cấu hình và `git diff --check`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `AGENTS.md`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Môi trường biên dịch Java của app và các module thư viện.
