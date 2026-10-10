# Youtube Downloader

An Android application built for video and audio downloading, stream previewing, and local media management.

---

## 📌 Project Context
This side project was built **for fun and continuous learning** while working full-time at **Qualtrics** to stay ahead with the latest Android development standards, Jetpack libraries, and modern Kotlin practices.

---

## 🛠 Tech Stack & Architecture

### **Architecture & Engineering Practices**
* **Multi-Module Clean Architecture:** Decoupled codebase divided into `:app`, `:navigation`, `:core:*`, `:features:*`, and `:testing:*` modules.
* **Unidirectional Data Flow (UDF):** Predictable UI state management powered by Kotlin `StateFlow` and `ViewModel`.

### **Libraries & Tooling**
* **UI & Design:** Jetpack Compose, Material 3, Compose Navigation, Coil 3 (Image Loading).
* **Dependency Injection:** Dagger Hilt with modular DI setup.
* **Asynchronous Programming:** Kotlin Coroutines, Flow.
* **Media Playback:** Jetpack Media3 (`ExoPlayer`, `PlayerView`).
* **Background Processing:** Lifecycle-aware Android `ForegroundService` with dynamic notification progress reporting.
* **Database & Persistence:** Room Database and MediaStore integration (`MediaScannerConnection`).
* **Engine Core:** `yt-dlp` native library integration for YouTube link parsing and stream extraction.

---

## 📦 Sub-Module Structure

```
.
├── app                  # Application entry point & nav graph
├── navigation           # Navigation destinations & transitions
├── core
│   ├── common           # Shared utilities & extensions
│   ├── designsystem     # Theme, typography, color scheme, shapes
│   ├── network          # Network downloader client & yt-dlp engine wrapper
│   └── storage          # Room database, DAOs, and entities
├── features
│   ├── downloader       # URL parsing, format selection, Foreground Service & player
│   └── history          # Download history list, playback, sharing & deletion
└── testing
    ├── ui               # UI test helpers & configurations
    └── unit             # Unit test mocks & utilities
```

---

## 🔮 Future Improvements & Roadmap

While the app core functionality is complete, future iterations plan to focus on:

* **Testing Coverage:**
  * Implement comprehensive Unit Tests (JUnit 5, MockK, Coroutine Test Dispatchers) across `ViewModel`s and Repositories.
  * Add UI / End-to-End Tests using Compose Test Rule in `:testing:ui`.
* **In-app search functionality:**
  * Add support for searching for videos inside the app, rather than only allowing YT links.
