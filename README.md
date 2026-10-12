# CodeBaseV2

Android base library gồm `core`, `core:ads` và `core:baseui`, sử dụng Kotlin, Java 17, Hilt, KSP và ViewBinding.

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

## Core Ads

`CoreActivity` hỗ trợ ViewBinding và preload dùng chung cho Interstitial, Rewarded và App Open. Cấu hình quảng cáo theo placement:

```kotlin
override fun provideCoreAdConfig(): CoreAdConfig {
    return CoreAdConfig(
        placements = listOf(
            CoreAdPlacement(
                placement = "home_inter",
                type = CoreAdType.INTERSTITIAL,
                adUnitIds = listOf("inter-high-floor", "inter-fallback"),
            ),
            CoreAdPlacement(
                placement = "unlock_rewarded",
                type = CoreAdType.REWARDED,
                adUnitIds = listOf("rewarded-id"),
            ),
            CoreAdPlacement(
                placement = "app_open",
                type = CoreAdType.APP_OPEN,
                adUnitIds = listOf("app-open-id"),
            ),
        ),
    )
}
```

Cache được chia theo `adType + adUnitId`, nên nhiều placement dùng chung một ID sẽ không load trùng. Có thể cấu hình retry, cooldown, giới hạn Interstitial theo session và priority từ Firebase Remote Config; xem hướng dẫn đầy đủ tại [docs/core-ads.md](docs/core-ads.md).

Nếu dùng Hilt, Application của app sử dụng thư viện cần kế thừa base class trong `ads`:

```kotlin
@HiltAndroidApp
class AppApplication : CodeBaseApplication()
```
