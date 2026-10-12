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

### Use English status terms in Android skill

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Replace the Vietnamese status labels in the skill wording with the English equivalents `completed`, `paused`, and `abandoned`.
  - Keep the repository's existing `PLAN.md` status values unchanged.
- **Unit test cần viết/chạy:**
  - No unit test; this is a documentation-only change.
  - Check the skill text and run `git diff --check`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - None at runtime; only agent guidance wording changes.

### Refine English wording in Android skill

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Rewrite the work-item completion instruction in clear English while preserving the exact Vietnamese status values required by `PLAN.md`.
- **Unit test cần viết/chạy:**
  - No unit test; this is a documentation-only change.
  - Check the skill text and run `git diff --check`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - None at runtime; only agent guidance wording changes.

### Tái cấu trúc skill Android theo nhóm quy tắc

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Sắp xếp lại skill thành các nhóm: phạm vi, baseline project, UI/resource, build/dependency, workflow, kiểm thử và giới hạn thay đổi.
  - Giữ nguyên các quy tắc đã thống nhất nhưng loại bỏ cảm giác rải rác và trùng ý.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test; chỉ thay đổi hướng dẫn của agent.
  - Chạy validator của skill nếu có và kiểm tra `git diff --check`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Cách agent lựa chọn widget, resource, dependency, quy trình sửa code và kiểm thử Android.

### Chuẩn hóa resource cho text và color UI

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Chuyển text hiển thị trong XML và Kotlin của màn hình test quảng cáo sang `res/values/strings.xml`.
  - Chuyển trạng thái động của callback quảng cáo sang string resource có placeholder.
  - Quy định mọi color UI phải khai báo trong `res/values/colors.xml`, không hard-code trong XML hoặc Kotlin.
  - Cập nhật skill Android để giữ quy ước này cho các thay đổi sau.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test JVM; thay đổi chỉ là resource và UI wiring.
  - Kiểm tra `git diff --check`; chạy `:app:assembleDebug` nếu môi trường Gradle hoạt động.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values/colors.xml`
  - `app/src/main/res/layout/activity_main.xml`
  - `app/src/main/java/com/sabo/test/MainActivity.kt`
  - `app/src/debug/res/layout/activity_ads_ui_test.xml`
  - `app/src/debug/java/com/sabo/test/AdsUiTestActivity.kt`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Nội dung hiển thị và trạng thái kiểm tra của màn hình quảng cáo mẫu.

### Chuẩn hóa widget XML sang AppCompat trong skill Android

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Bổ sung vào skill quy ước ưu tiên `AppCompatTextView`, `AppCompatButton` và `AppCompatImageView` thay cho widget nền tảng `TextView`, `Button`, `ImageView` trong XML.
  - Chuyển các widget tương ứng trong giao diện test quảng cáo hiện tại sang bản AppCompat.
  - Bổ sung quy ước ưu tiên `NestedScrollView` thay cho `ScrollView` và áp dụng cho giao diện test hiện tại.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test JVM; thay đổi là quy ước UI và class widget XML.
  - Kiểm tra `git diff --check`; chạy `:app:assembleDebug` nếu môi trường Gradle hoạt động.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `app/src/main/res/layout/activity_main.xml`
  - `C:\Users\Tuan Anh\.codex\skills\codebasev2-android\SKILL.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Khả năng tương thích theme và hiển thị các control trong màn hình test quảng cáo.

### Thêm giao diện test quảng cáo vào MainActivity

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Thay màn hình mẫu bằng giao diện có nút thật để gọi Interstitial, Rewarded và App Open.
  - Hiển thị trạng thái callback để kiểm tra nhanh việc load/show quảng cáo trên emulator hoặc thiết bị.
  - Giữ nguyên các placement và test ad unit ID đang có trong `MainActivity`.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test JVM; thay đổi chủ yếu là UI wiring và callback của SDK quảng cáo.
  - Đã thử `:app:assembleDebug`, nhưng Gradle không tạo được loopback connection trong môi trường hiện tại; cần chạy lại trên máy có Gradle daemon hoạt động bình thường.
  - `:app:connectedDebugAndroidTest` cần emulator/device nên chưa chạy trong lượt này.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `app/src/main/res/layout/activity_main.xml`
  - `app/src/main/java/com/sabo/test/MainActivity.kt`
- **Các tính năng có thể bị ảnh hưởng:**
  - Màn hình mẫu dùng để kiểm tra thủ công Interstitial, Rewarded và App Open.

### Thêm UI test cho các luồng quảng cáo full-screen

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Thêm Activity debug-only làm test host cho `CoreActivity` và ba API Interstitial, Rewarded, App Open.
  - Dùng cấu hình rỗng để kiểm tra nhánh không có cache, không phụ thuộc mạng/quảng cáo thật.
  - Viết instrumentation test mở Activity, kiểm tra UI, bấm từng nút và xác nhận callback không hiển thị quảng cáo.
- **Unit test cần viết/chạy:**
  - `:app:connectedDebugAndroidTest` nếu có emulator/device.
  - Đã thêm `app/src/androidTest/java/com/sabo/test/AdsUiTest.kt`.
  - Đã thử compile `:app:compileDebugKotlin` và `:app:compileDebugAndroidTestKotlin`, nhưng Gradle không tạo được loopback connection trong môi trường hiện tại; chưa thể chạy connected test nếu không có emulator/device.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `app/src/debug/`
  - `app/src/androidTest/`
- **Các tính năng có thể bị ảnh hưởng:**
  - UI test và nhánh fallback khi Interstitial, Rewarded hoặc App Open chưa có cache.

### Bổ sung ghi chú cho các biến cấu hình quảng cáo

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Thêm KDoc cho các biến public trong model placement, policy và kết quả quảng cáo.
  - Ghi rõ đơn vị thời gian, giá trị mặc định và tác động của từng biến khi cấu hình từ IDE.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test; thay đổi chỉ bổ sung tài liệu trong mã nguồn.
  - Kiểm tra `git diff --check`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `core/ads/src/main/java/com/core/ads/CoreAdConfig.kt`
- **Các tính năng có thể bị ảnh hưởng:**
  - Không thay đổi hành vi runtime; cải thiện khả năng sử dụng API cấu hình quảng cáo.

### Bổ sung tài liệu sử dụng Core Ads

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Tạo tài liệu chi tiết trong `docs/` cho cấu hình placement, waterfall, cache, policy và Remote Config.
  - Cập nhật `README.md` với hướng dẫn cài JitPack và ví dụ dùng `CoreActivity`/`CoreAdConfig`.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test; chỉ thay đổi tài liệu.
  - Kiểm tra liên kết, ví dụ Kotlin và `git diff --check`.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `docs/core-ads.md`
  - `README.md`
- **Các tính năng có thể bị ảnh hưởng:**
  - Tài liệu tích hợp thư viện Core, Core Ads và CoreActivity.

### Tối ưu mô hình cấu hình quảng cáo theo CodeBase mẫu

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Tách cấu hình placement khỏi cấu hình policy dùng chung của quảng cáo full-screen.
  - Hỗ trợ waterfall nhiều ad unit id, bật/tắt, preload, tự load lại sau dismiss và bỏ qua interval theo placement.
  - Thêm policy retry chung, policy chống spam Interstitial, policy retry context của Rewarded và policy cooldown/reopen của App Open.
  - Giữ cache quảng cáo theo cặp `adType + adUnitId`, không tạo request trùng khi nhiều placement dùng chung ID.
  - Cho phép adapter Remote Config override thứ tự ưu tiên nhưng vẫn có fallback local.
- **Unit test cần viết/chạy:**
  - Viết unit test cho việc lọc/sắp xếp placement, waterfall và fallback policy thuần Kotlin.
  - Đã thêm `core/ads/src/test/java/com/core/ads/CoreAdConfigTest.kt`.
  - Đã thử chạy `:core:ads:testDebugUnitTest`, `:core:baseui:testDebugUnitTest`, `:app:assembleDebug` nhưng Gradle không tạo được loopback connection trong môi trường hiện tại; cần chạy lại trong Android Studio hoặc terminal máy local.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `core/ads/src/main/java/com/core/ads/CoreAdConfig.kt`
  - `core/ads/src/main/java/com/core/ads/AdPreloadManager.kt`
  - `core/baseui/src/main/java/com/core/baseui/CoreActivity.kt`
  - `app/src/main/java/com/sabo/test/MainActivity.kt`
  - `core/ads/src/test/`
- **Các tính năng có thể bị ảnh hưởng:**
  - Cấu hình, preload, cache và hiển thị Interstitial, Rewarded và App Open.

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

### Thêm CoreActivity và preload quảng cáo ưu tiên

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Đối chiếu `CoreActivity`, `BaseActivity` và `ContextAds` của project mẫu `D:\Android\CodeTA\CodeBase`.
  - Tạo `AdPreloadManager` singleton trong `:core:ads` để cache và preload tối đa hai interstitial cùng một rewarded.
  - Ưu tiên nhóm Inter 1, Inter 2 và Reward; khởi động ba request song song để một request chậm không chặn các slot còn lại.
  - Không load trùng, có timeout và tự nạp lại sau khi quảng cáo được sử dụng.
  - Tạo `CoreActivity<VB>` trong `:core:baseui` dùng ViewBinding và cung cấp API show interstitial/rewarded.
  - Cấu hình Google Mobile Ads dependency và ViewBinding cho các module cần thiết.
  - Tích hợp `MainActivity` làm ví dụ sử dụng `CoreActivity`.
- **Unit test cần viết/chạy:**
  - Không thêm unit test JVM riêng vì logic phụ thuộc trực tiếp vào callback/SDK Google Mobile Ads; Gradle đã chạy các task unit test của hai module, hiện không có test source.
  - `:core:ads:testDebugUnitTest`, `:core:baseui:testDebugUnitTest` và `:app:assembleDebug` — đạt.
  - Không chạy connected test nếu không có emulator/device khả dụng.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `gradle/libs.versions.toml`
  - `core/ads/build.gradle.kts`
  - `core/ads/src/main/java/com/core/ads/AdPreloadManager.kt`
  - `core/ads/src/main/java/com/core/ads/di/AdPreloadEntryPoint.kt`
  - `core/baseui/build.gradle.kts`
  - `core/baseui/src/main/java/com/core/baseui/CoreActivity.kt`
  - `app/build.gradle.kts`
  - `app/src/main/java/com/sabo/test/MainActivity.kt`
- **Các tính năng có thể bị ảnh hưởng:**
  - API Activity dùng chung, preload/show quảng cáo interstitial và rewarded, thời gian hiển thị quảng cáo và build dependency của app.

### Typed ad config và cache theo loại quảng cáo

- **Ngày khởi tạo:** 2026-10-12
- **Ngày thực hiện:** 2026-10-12
- **Ngày hoàn thành:** 2026-10-12
- **Trạng thái:** `hoàn thành`
- **Công việc chi tiết cần thực hiện:**
  - Thêm type cấu hình `INTERSTITIAL`, `REWARDED` và `APP_OPEN` cùng `adUnitId` và `priority`.
  - Cho phép remote config override `priority`, giữ `priority` trong code làm fallback khi remote chưa có dữ liệu.
  - Không buộc `:core` phụ thuộc Firebase; app tự triển khai adapter `CoreAdsRemoteConfig` cho Firebase Remote Config hoặc backend hiện có.
  - Refactor cache theo khóa `type + adUnitId` thay vì cache cứng theo slot.
  - Không tạo request trùng khi hai slot dùng cùng một ad unit ID.
  - Bổ sung preload/show App Open dùng chung trong `AdPreloadManager`.
  - Cập nhật app mẫu sang cấu hình typed ad.
- **Unit test cần viết/chạy:**
  - Không phát sinh unit test JVM riêng nếu logic vẫn phụ thuộc trực tiếp vào Google Mobile Ads SDK; chạy lại các task build/unit test hiện có.
  - `:core:ads:testDebugUnitTest`, `:core:baseui:testDebugUnitTest` và `:app:assembleDebug` — đạt.
- **Các file ảnh hưởng:**
  - `PLAN.md`
  - `core/ads/src/main/java/com/core/ads/CoreAdConfig.kt`
  - `core/ads/src/main/java/com/core/ads/AdPreloadManager.kt`
  - `core/baseui/src/main/java/com/core/baseui/CoreActivity.kt`
  - `app/src/main/java/com/sabo/test/MainActivity.kt`
- **Các tính năng có thể bị ảnh hưởng:**
  - Cấu hình, preload và hiển thị Interstitial, Rewarded và App Open.
