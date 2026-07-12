# Android Project Blueprint: Modular Clean Architecture

## 1. Project Philosophy
This project is built on **Modular Clean Architecture**. It is designed for maximum testability, scalability, and build efficiency by strictly separating business logic from implementation details.

## 2. Architecture Overview

### Modularization Strategy: **Feature-by-Layer**
The project is divided into three main tiers to prevent circular dependencies and promote code reuse:

| Tier | Module | Responsibility |
| :--- | :--- | :--- |
| **App** | `:app` | Application class, Hilt entry points, and global navigation host. |
| **Core** | `:core:network` | Retrofit/OkHttp, common DTOs, and API result wrappers. |
| | `:core:designsystem` | Theme, shared UI components, and design tokens (Colors/Fonts). |
| | `:core:navigation` | Global route definitions and decoupled navigation APIs. |
| **Features** | `:features:[name]` | Self-contained business units (e.g., `:features:onboarding`). |

---

## 3. Feature Module Structure
Every feature module follows a 3-layer internal structure:

### 1. Domain Layer (`domain/`)
*The "Brain": Pure Kotlin, no Android dependencies.*
- **`model/`**: Business entities.
- **`repository/`**: Interfaces defining data needs.
- **`service/` or `usecase/`**: Pure business logic and calculations.

### 2. Data Layer (`data/`)
*The "Provider": Implementation details.*
- **`remote/`**: Retrofit API interfaces and DTOs.
- **`repository/`**: Implementation of domain repository interfaces.
- **`mapper/`**: Converts DTOs to Domain models.

### 3. Presentation Layer (`presentation/`)
*The "Face": UI and State.*
- **`ui/`**: Compose Screens and local components.
- **`viewmodel/`**: Hilt ViewModels managing a single `StateFlow`.
- **`ui/model/`**: UI-specific entities (e.g., items with resource IDs).

---

## 4. Key Patterns & Rules

### Unidirectional Data Flow (UDF)
Viewmodels must expose a single `UiState` object. The UI emits events to the ViewModel, and the ViewModel updates the state.
```kotlin
data class FeatureUiState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val errorResId: Int? = null
)
```

### Decoupled Navigation
Feature modules **never** depend on each other.
- Routes are defined in `:core:navigation`.
- Feature modules implement a `FeatureNavigationApi` to register themselves in the global `NavHost`.

### Resource Handling (i18n)
- **Zero hardcoded strings** in code.
- ViewModels pass `@StringRes Int` IDs to the UI.
- The UI layer resolves strings via `stringResource(id)`.

---

## 5. Tech Stack
- **Language**: Kotlin 2.x
- **UI**: Jetpack Compose (Material 3)
- **DI**: Hilt
- **Async**: Coroutines & Flow
- **Network**: Retrofit + Kotlinx Serialization
- **Navigation**: Type-safe Compose Navigation
- **Testing**: MockK, JUnit4, Compose Test Rule

---

## 6. Development Guidelines for AI Agents
When generating code for this project, please follow these steps:
1. **Domain First**: Create the data models and repository interfaces.
2. **Contract Logic**: Implement the business logic (Services/UseCases) with Unit Tests.
3. **Data Implementation**: Create the API service and the Repository implementation.
4. **UI State**: Define a single `UiState` and the corresponding `ViewModel`.
5. **Compose UI**: Build the UI components using the shared `:core:designsystem`.

---

## 7. Build & Setup
1. Clone the repository.
2. Sync Gradle.
3. Run `:app` to launch the application.
4. Run `./gradlew test` to execute all module unit tests.
