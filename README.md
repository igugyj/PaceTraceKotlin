# PaceTraceKotlin

> Android client for PaceTrace -- a university sports run tracking & club sign-in assistant.

## Features

- **Run Management** -- View run standards, semester progress, submit run records (with track simulation)
- **Club Sign-in** -- Browse club activities, join/leave clubs, activity sign-in/sign-back (with automatic coordinate offset)
- **Profile** -- View user info, run statistics, semester scores
- **Route Selection** -- Built-in campus route maps (GCJ-02 coordinate system)

## Requirements

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17+
- **Android SDK** 34
- **Gradle** 8.2+

## Downloads

Pre-built APKs are available on the [Releases](https://github.com/igugyj/PaceTraceKotlin/releases) page. Download and install directly on your device.

## Build from Source

1. Clone the repo:

```bash
git clone https://github.com/igugyj/PaceTraceKotlin.git
```

2. Configure API keys:

```bash
cp secrets.properties.example secrets.properties
```

Edit `secrets.properties` with your actual `APPKEY` and `APPSECRET`.

3. Open the project in Android Studio, sync Gradle, then run on a device or emulator.

Developers are welcome to fork, modify, and build their own versions.

## secrets.properties

| Field | Description | Default |
|-------|-------------|---------|
| `APPKEY` | API app key | — |
| `APPSECRET` | API app secret | — |
| `BASE_URL` | Backend API base URL | `https://run-lb.tanmasports.com/` |
| `UA` | User-Agent | `okhttp/3.10.0` |

> **Security**: `secrets.properties` is gitignored. Never commit real keys.

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **OkHttp** + **Gson**
- **Navigation Compose**
- **Coroutines**

## Project Structure

```
app/src/main/java/com/pacetrace/app/
├── MainActivity.kt          # Entry Activity
├── PaceTraceApp.kt           # Application class (global crash handler)
├── api/                      # Network layer
│   ├── ApiClient.kt          # MD5-signed API client
│   ├── AppContext.kt         # Global context (user/token)
│   ├── AuthApi.kt            # Auth endpoints
│   ├── ClubApi.kt            # Club endpoints
│   ├── Models.kt             # Data models
│   └── RunApi.kt             # Run endpoints
├── lib/                      # Utilities
│   ├── GeoUtil.kt            # Coordinate offset, route distance, track generation
│   ├── MapsUtil.kt           # Route map loading
│   └── TimeUtil.kt           # Time parsing
└── ui/                       # UI layer
    ├── MainScreen.kt         # Main navigation
    ├── club/ClubScreen.kt
    ├── home/HomeScreen.kt
    ├── login/LoginScreen.kt
    ├── profile/ProfileScreen.kt
    ├── run/RunScreen.kt
    └── theme/Theme.kt        # Material 3 theme
```

## Related Projects

- [PaceTrace](https://github.com/igugyj/PaceTrace) -- Python (Streamlit) desktop version with background auto sign-in/run schedulers

## License

[CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/)
