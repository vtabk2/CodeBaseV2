# AGENTS.md

## Quy trình bắt buộc trước khi code

1. Đọc `PLAN.md` ở thư mục gốc trước khi thay đổi bất kỳ file nào.
2. Với mọi thay đổi ảnh hưởng đến logic, hành vi, UI, dữ liệu, build hoặc cấu hình, tạo một mục công việc trong `PLAN.md` trước khi sửa code.
3. Mỗi mục trong `PLAN.md` phải có ngày khởi tạo, ngày thực hiện, ngày hoàn thành, trạng thái, công việc chi tiết, unit test cần viết/chạy, file ảnh hưởng và tính năng có thể bị ảnh hưởng.
4. Cập nhật trạng thái và ngày tháng khi bắt đầu, tạm hoãn, hoàn thành hoặc từ bỏ công việc.
5. Thay đổi logic thuần Kotlin phải có unit test tập trung. Không tạo test hình thức nếu thay đổi không có logic kiểm thử được; ghi rõ lý do trong `PLAN.md`.

## Quy ước Android của project

- Project có một module ứng dụng (`:app`) và module thư viện cha `:core` với hai module con `:core:ads`, `:core:baseui`.
- Dùng Kotlin, Gradle Kotlin DSL và version catalog tại `gradle/libs.versions.toml`.
- UI hiện dùng XML, AppCompat, Material và ConstraintLayout; không tự chuyển sang Jetpack Compose nếu chưa được yêu cầu.
- Namespace/application ID: `com.sabo.test`.
- Compile/target SDK: 36; min SDK: 24; Java source/target: 11.
- Unit test đặt trong `app/src/test`; instrumentation/UI test đặt trong `app/src/androidTest`.
- Dependency phải khai báo trong `gradle/libs.versions.toml`, sau đó tham chiếu alias trong `app/build.gradle.kts`.

## Kiểm tra sau khi thay đổi

Chạy các lệnh phù hợp với phạm vi thay đổi:

```powershell
./gradlew.bat :app:testDebugUnitTest
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:connectedDebugAndroidTest
```

Nếu môi trường thiếu Java, Android SDK, emulator/device hoặc network, ghi rõ blocker trong kết quả công việc và vẫn kiểm tra diff thủ công.

## CodeGraph

Nếu repository có thư mục `.codegraph/`, dùng CodeGraph trước `grep`, `find` hoặc đọc file để định vị và hiểu code. Nếu không có `.codegraph/`, bỏ qua CodeGraph.

## An toàn thay đổi

- Giữ nguyên thay đổi có sẵn của người dùng; không dùng lệnh phá hủy như `git reset --hard` hoặc `git checkout --` nếu chưa được yêu cầu.
- Không commit secrets, signing material, file build sinh ra hoặc đường dẫn máy cục bộ.
- Sau khi sửa, kiểm tra `git diff` và `git status --short`.
