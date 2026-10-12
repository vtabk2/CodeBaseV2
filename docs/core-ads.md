# Core Ads

Tài liệu này mô tả cách sử dụng `core:ads` và `core:baseui` trong project ứng dụng.

## Cài đặt

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.vtabk2:CodeBaseV2:1.0.0")
}
```

`core` đã kéo theo `core:ads` và `core:baseui`.

## Application và Hilt

`CodeBaseApplication` nằm trong package `com.core.ads`. Application của app kế thừa class này và vẫn cần là Hilt root:

```kotlin
@HiltAndroidApp
class AppApplication : CodeBaseApplication()
```

Khai báo Application trong `AndroidManifest.xml` và thêm App ID của Google Mobile Ads:

```xml
<application
    android:name=".AppApplication"
    ...>
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy" />
</application>
```

## Cấu hình placement

Mỗi `CoreAdPlacement` là một vị trí logic và có thể chứa nhiều ad unit ID theo thứ tự waterfall:

```kotlin
override fun provideCoreAdConfig(): CoreAdConfig {
    return CoreAdConfig(
        placements = listOf(
            CoreAdPlacement(
                placement = "home_inter",
                type = CoreAdType.INTERSTITIAL,
                adUnitIds = listOf(
                    "ca-app-pub-xxx/high-floor",
                    "ca-app-pub-xxx/fallback",
                ),
                priority = 0,
                enabled = true,
                preload = true,
                autoReloadAfterDismiss = true,
            ),
            CoreAdPlacement(
                placement = "unlock_rewarded",
                type = CoreAdType.REWARDED,
                adUnitIds = listOf("ca-app-pub-xxx/rewarded"),
            ),
            CoreAdPlacement(
                placement = "app_open",
                type = CoreAdType.APP_OPEN,
                adUnitIds = listOf("ca-app-pub-xxx/app-open"),
            ),
        ),
    )
}
```

Các trường chính:

| Trường | Ý nghĩa |
| --- | --- |
| `placement` | Tên vị trí dùng khi gọi quảng cáo |
| `type` | `INTERSTITIAL`, `REWARDED` hoặc `APP_OPEN` |
| `adUnitIds` | Waterfall ID; ID load trước được đặt trước |
| `enabled` | Bật/tắt placement |
| `preload` | Cho phép placement tham gia preload |
| `priority` | Thứ tự local giữa các placement cùng loại |
| `autoReloadAfterDismiss` | Load lại sau khi quảng cáo đóng |
| `ignoreInterval` | Bỏ qua giới hạn thời gian hiển thị |
| `extraIntervalSeconds` | Cộng thêm cooldown riêng cho Interstitial |

Manager preload tối đa hai placement Interstitial ưu tiên nhất, một Rewarded và một App Open. Các request được chạy song song giữa các placement.

## Cache và waterfall

Cache dùng khóa:

```text
(adType, adUnitId)
```

Vì vậy hai placement cùng loại dùng chung một ID sẽ dùng chung một SDK load result và không tạo request trùng. Interstitial và Rewarded dùng cùng chuỗi ID vẫn là hai cache khác nhau.

Khi một ID trong waterfall lỗi, manager thử ID tiếp theo. Cache full-screen có thời hạn nội bộ 55 phút; App Open có thời hạn 4 giờ.

## Policy

```kotlin
CoreAdConfig(
    fullscreenPolicy = CoreFullscreenPolicy(
        waitLoadToShow = false,
        enableRetry = true,
        maxRetryCount = 5,
        retryIntervalSeconds = listOf(3, 6, 9, 12, 15),
    ),
    interstitialPolicy = CoreInterstitialPolicy(
        adsPerSession = 50,
        sessionDurationSeconds = 600,
        intervalSeconds = 37,
        intervalAfterAppOpenSeconds = 30,
        meaningfulActionsBetweenAds = 1,
    ),
    rewardedPolicy = CoreRewardedPolicy(
        maxRetryOnContext = 2,
        retryDelayOnContextSeconds = 5,
    ),
    appOpenPolicy = CoreAppOpenPolicy(
        delayBeforeShowMillis = 200,
        intervalSeconds = 3600,
        reopenMode = CoreAppOpenReopenMode.APP_OPEN,
    ),
)
```

Sau một hành động có ý nghĩa, Activity có thể gọi:

```kotlin
recordCoreMeaningfulAction()
```

## Dùng với CoreActivity

`CoreActivity<VB>` dùng ViewBinding và tự preload khi `onCreate`:

```kotlin
@AndroidEntryPoint
class MainActivity : CoreActivity<ActivityMainBinding>() {
    override fun createViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onViewReady(savedInstanceState: Bundle?) = Unit

    private fun showAds() {
        showInterstitial("home_inter") { isShown ->
            // Continue flow.
        }

        showRewarded { result ->
            if (result.isEarned) {
                // Unlock the feature.
            }
        }

        showAppOpen()
    }
}
```

Nếu không truyền tên placement cho Interstitial, manager sẽ tìm trong hai placement Interstitial ưu tiên đầu tiên.

## Remote Config

Core không phụ thuộc Firebase. App triển khai adapter riêng:

```kotlin
override fun provideCoreAdsRemoteConfig(): CoreAdsRemoteConfig {
    return object : CoreAdsRemoteConfig {
        override fun priorityFor(type: CoreAdType, adUnitId: String): Int? {
            return remotePriorityForAdUnit(type, adUnitId)
        }

        override fun placementPriority(type: CoreAdType, placement: String): Int? {
            return remotePriorityForPlacement(type, placement)
        }
    }
}
```

Nếu Remote Config trả về `null`, thứ tự local trong `priority` và `adUnitIds` được giữ nguyên.

### Firebase Remote Config

Ứng dụng dùng Firebase Remote Config qua `com.core.ads.FirebaseAdsRemoteConfig`. Adapter nằm
trong `:core:ads`, tự đọc danh sách placement từ Firebase và chuyển thành `CoreAdPlacement` để
mọi app dùng thư viện có thể tái sử dụng cùng logic.

Tạo hai parameter trong Firebase Console. Quảng cáo full-screen Interstitial và Rewarded nằm
chung trong một JSON array:

- `rewarded__rewarded_inter__inter_ad_places`
- `app_open_ad_places`

```json
[
  {
    "place_name": "home_inter_primary",
    "ad_id": "ca-app-pub-xxx/inter-primary",
    "high_floor_ad_ids": ["ca-app-pub-xxx/inter-fallback"],
    "ad_type": "interstitial",
    "is_enable": true,
    "priority": 0
  },
  {
    "place_name": "unlock_rewarded",
    "ad_id": "ca-app-pub-xxx/rewarded",
    "ad_type": "rewarded",
    "is_enable": true,
    "priority": 0
  }
]
```

`app_open_ad_places` dùng cùng cấu trúc nhưng chứa các object có `ad_type` là `app_open`.
Các trường được adapter đọc gồm `place_name`, `ad_id`, `high_floor_ad_ids`, `ad_type`,
`is_enable`, `is_preload`, `priority`, `is_auto_load_after_dismiss`, `is_tutorial_flow`,
`is_tracking_click`, `is_tracking_show` và `plus_interval`.

Khai báo adapter một lần bằng Hilt `@Provides @Singleton`. `CodeBaseApplication` lấy binding này
sớm trong lifecycle, còn `CoreActivity` tự dùng instance, preload local fallback trước, fetch Firebase
và preload lại sau khi Remote Config activate:

```kotlin
@HiltAndroidApp
class AppApplication : CodeBaseApplication()

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {
    @Provides
    @Singleton
    fun provideFirebaseAdsRemoteConfig(@ApplicationContext context: Context): FirebaseAdsRemoteConfig {
        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        val interval = if (isDebuggable) {
            FirebaseAdsRemoteConfig.DEBUG_MINIMUM_FETCH_INTERVAL_SECONDS
        } else {
            FirebaseAdsRemoteConfig.DEFAULT_MINIMUM_FETCH_INTERVAL_SECONDS
        }
        return FirebaseAdsRemoteConfig(interval)
    }
}
```

`FirebaseAdsRemoteConfig.getAdPlacements()` trả về danh sách đã parse nếu Activity cần tự xây
`CoreAdConfig`. Trong mẫu app, `MainActivity` chỉ đọc danh sách từ `CoreActivity` và dùng cấu hình
local khi Firebase chưa có dữ liệu; Activity không tự khởi tạo adapter.

`core:ads` đã khai báo Firebase Android BoM và `firebase-config`. Đặt `google-services.json` tại thư mục
`app/` của ứng dụng tiêu thụ thư viện; build script sẽ tự bật Google services plugin khi file này tồn tại. Nếu chưa cấu hình
Firebase, adapter tự fallback về priority local và không làm crash app.

Giá trị fallback được khai báo theo cùng cấu trúc với project `CodeBase` tại
[`core/ads/src/main/res/xml/remote_config_defaults.xml`](../core/ads/src/main/res/xml/remote_config_defaults.xml).
Root XML dùng `<defaults>`, mỗi `<entry>` là một parameter và JSON nằm trong `<value>`.
Đây không phải dữ liệu Remote Config production; file chỉ được dùng trước khi Firebase fetch thành công
hoặc khi Firebase chưa được cấu hình. Ứng dụng dùng JitPack có thể tạo resource cùng tên trong
`app/src/main/res/xml/remote_config_defaults.xml` để ghi đè bộ default cho app đó.

Fetch interval được quyết định ở Hilt module của app tiêu thụ thư viện bằng
`ApplicationInfo.FLAG_DEBUGGABLE`, không import `BuildConfig` của app vào thư viện/AAR JitPack.
Cách này đọc đúng variant đang chạy và không phụ thuộc package của app. Khuyến nghị dùng
`debug = 0` giây để test lấy cấu hình mới ngay và `release = 3.600` giây để hạn chế request
không cần thiết.
