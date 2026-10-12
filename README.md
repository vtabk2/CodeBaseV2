# CodeBaseV2

## JitPack

Tạo một GitHub Release hoặc tag, sau đó thêm repository và dependency vào project sử dụng thư viện:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.vtabk2:CodeBaseV2:1.0.0")
}
```

`core` kéo theo hai module con `ads` và `baseui`.

Nếu dùng Hilt, Application của app sử dụng thư viện cần kế thừa base class trong `ads`:

```kotlin
@HiltAndroidApp
class AppApplication : CodeBaseApplication()
```
