# Task Manager Android Application (Clean Architecture & MVVM)

A high-fidelity, production-grade Android Task Manager built from scratch using **Jetpack Compose**, **Material Design 3**, and strict **Clean Architecture / MVVM** guidelines. It integrates multi-source syncing (Retrofit network + Room local cache) with custom UI flows, robust state hoisting, and local JVM tests.

---

## 🛠️ Tech Stack & Key Choices

* **Kotlin & Coroutines/Flow**: Handles asynchronous execution and responsive, reactive data streams seamlessly.
* **Jetpack Compose & Material 3**: Visual layouts feature unified surface elevations, a crisp color scheme with soft pastel indicators, edge-to-edge layout safe area insertions, and an adaptive layout design.
* **Navigation Compose**: Configures Single Activity navigation via state-preserving screen composables.
* **Decoupled Manual DI (AppContainer Pattern)**: Instantiates dependency modules (`DatabaseModule`, `NetworkModule`, `RepositoryModule`) in a global Application state container. This guarantees type-safety and robust compile speeds while maintaining clear horizontal isolation boundaries without dagger/compiler bloat.
* **Room Database**: Local SQLite caching with Flow emission support.
* **Retrofit & Moshi**: Consumes raw JSON Todos from `dummyjson.com/todos` as remote items.

---

## 🏗️ Folder Structure

```
/app/src/main/java/com/example/
├── TaskManagerApplication.kt      # Application Class - Instantiates global AppContainer
├── MainActivity.kt                # Single Entry Activity - Configures Edge-to-Edge & NavGraph
│
├── di/                            # Dependency Injection Modules
│   ├── AppContainer.kt            # Aggregates Modules into a Single Graph
│   ├── DatabaseModule.kt          # Declares Room RoomDatabase and Dao instances
│   ├── NetworkModule.kt           # Declares OkHttpClient logs, Moshi adapters & Retrofit
│   └── RepositoryModule.kt        # Delivers concrete Repo classes with interfaces
│
├── domain/                        # Pure Domain Layer (Framework independent)
│   ├── model/
│   │   ├── Task.kt                # Core Domestic Model (Title, Description, Priority, Due Date)
│   │   └── Priority.kt            # Enum expressing LOW, MEDIUM, or HIGH priority
│   ├── repository/
│   │   └── TaskRepository.kt      # Abstract Contract repository boundary interface
│   └── usecase/
│       ├── AddTaskUseCase.kt      # Business rule for local task insertion
│       ├── DeleteTaskUseCase.kt   # Business rule for task ejection
│       ├── GetTaskByIdUseCase.kt  # Business rule for detail loader
│       ├── GetTasksUseCase.kt     # Business rule for dynamic combined flows (search + filters)
│       ├── SyncTasksUseCase.kt    # Business rule triggering background network sync
│       └── UpdateTaskUseCase.kt   # Business rule for direct state updates
│
├── data/                          # Data Layer (Retrofit client, DB tables, mappings)
│   ├── database/
│   │   ├── AppDatabase.kt         # Room SQLite Database
│   │   ├── TaskDao.kt             # Data Access Object with reactive Flows
│   │   └── TaskEntity.kt          # DB Table Schema
│   ├── network/
│   │   ├── dto/
│   │   │   └── TodoDto.kt         # API JSON Moshi DTO wrappers matching DummyJson Todos
│   │   ├── TaskApi.kt             # Retrofit Routing definitions
│   │   └── NetworkResult.kt       # API wrapper catching 2xx successes, error codes, or exceptions
│   ├── mapper/
│   │   └── TaskMapper.kt          # DTO/Entity-to-Domain & Domain-to-Entity conversions
│   └── repository/
│       └── TaskRepositoryImpl.kt  # Repository implementation bridging Room & Retrofit
│
└── presentation/                  # Presentation Layer (M3 Composables & AAC ViewModels)
    ├── ViewModelFactory.kt        # Multi-ViewModel constructor factory mapping AppContainer graph
    ├── DateTimeUtils.kt           # Localized epoch date formatted helpers
    │
    ├── dashboard/
    │   ├── DashboardUiState.kt    # Analytical state variables
    │   ├── DashboardViewModel.kt  # Math calculators presenting statistics
    │   └── DashboardScreen.kt     # Screen showcasing analytics counters
    │
    ├── tasklist/
    │   ├── TaskListUiState.kt     # Filter, query, and lazy-list variables
    │   ├── TaskListViewModel.kt   # Reactive flows combiner mapping query filters
    │   └── TaskListScreen.kt      # Screen featuring queries, segment chips, and Empty States
    │
    ├── taskdetails/
    │   ├── TaskDetailsUiState.kt  # Details view variables
    │   ├── TaskDetailsViewModel.kt# Controls load, complete toggle, and safe deletes
    │   └── TaskDetailsScreen.kt   # Screen for task review, status check, and edit launchers
    │
    └── navigation/
        ├── Routes.kt              # String constants and parameter builders
        └── NavGraph.kt            # Compose NavHost connecting screen routing composables
```

---

## 🏛️ Comprehensive Architectural Strategy

We strictly follow **Clean Architecture** combined with **MVVM** and **Reactive flow observation**:

```
 ┌──────────────────────────────────────────────────────────────┐
 │                      Presentation Layer                      │
 │   [Compose UI Screns] ◄───[UiState]─── [AAC ViewModels]     │
 └───────────────────────────────┬──────────────────────────────┘
                                 │ Interacts via Use Cases only
 ┌───────────────────────────────▼──────────────────────────────┐
 │                         Domain Layer                         │
 │        [Use Cases] ────[Abstracts]───► [Core Models]         │
 └───────────────────────────────┬──────────────────────────────┘
                                 │ Delivers implementation
 ┌───────────────────────────────▼──────────────────────────────┐
 │                          Data Layer                          │
 │         [Room DB] ◄───[RepositoryImpl]───► [Retrofit API]    │
 └──────────────────────────────────────────────────────────────┘
```

1. **Horizontal Isolation**: The Domain Layer has **zero dependencies** on external frameworks, databases, or client runtimes. It is composed of pure Kotlin.
2. **Unidirectional Data Flow (UDF)**: User actions pass to ViewModels as event signals (e.g., `saveTask()`). ViewModels transform state using Kotlin Flow combiners and expose progress via modern Kotlin `StateFlow`. Composable screens observe state via life-cycle safe `collectAsState()` loops.
3. **Repository Pattern as a single source of truth**: Repositories abstract caching rules from the business layer. When the user demands "Pull-to-Refresh," the repo fetches items from standard REST API, clears previous remote objects from Room, and saves the fresh output. The UI doesn't know where the data comes from; it simply observes the Room Flow from `getTasksFlow()`.

---

## 🛡️ Senior Android Coding Interview Discussion Points

These discussion points demonstrate high-level engineering mastery:

### 1. Manual DI (Service Locator) vs. Dagger/Hilt
* **In-Depth View**: In complex monolithic platforms, third-party DI tools (like Hilt) generate heavy boilerplate and run extensive compile-time checks that slow down gradle builds. In this codebase, we use **Constructor Injection** backed by explicit provider modules aggregated under a lazy standard `AppContainer`.
* **Pragmatic Benefits**: Fully achieves dependency inversion, ensures 100% compile-time type-safety, bypasses code generation errors under complex Kotlin compilers, and permits simple mock substitutions in local JVM testing.

### 2. State Hoisting, UDF and Recomposites
* **State Structuring**: We strictly decouple UI layout renderers from mutable states. Text fields and filter buttons use hoisted state tokens.
* **Observation**: ViewModels use high-power coroutine operations like `flatMapLatest`, `combine`, and `stateIn` to merge search keys, priority filter types, and status conditions directly within the Flow layer rather than modifying private arrays inside ViewModels. This ensures that the UI recomposes *only* when the finalized filtered list shifts state, optimizing rendering performance on compact mobile screens.

### 3. Resilient Error-Handling Wrapper (`NetworkResult`)
* **Standard Failure**: Many codebases crash or fail silently when network timeouts or HTTP 500 exceptions occur.
* **Our Strategy**: All Retrofit operations are encapsulated by `NetworkResult` wrapped with safe try-catch handlers. Successes map directly to models; errors yield standard exceptions or actionable messages, allowing ViewModels to notify the UI in dynamic Snackbars via `SharedFlow` events.

### 4. Deterministic Testing
* **No Emulators**: Writing tests that require emulators is slow and unstable.
* **JVM Performance**: We provide **Fake Mock implementations** (`FakeTaskDao` and `FakeTaskApi`). These enable standard local JUnit and **Robolectric JVM tests** to confirm ViewModel logic, Repository mappings, and state changes instantly (executing in under 2 seconds).
* **Visual Regressions**: Combine Robolectric with **Roborazzi Screenshot Tests** (`GreetingScreenshotTest`) using Native Graphics rendering. This captures pixel-level rendering states (e.g. testing the empty-state Composable layout) directly on the host computer without requiring physical hardware connections.

---

## 🧪 Validating Tests

You can verify that all components, ViewModel states, repository mappers, and Compose UI screenshots compile and execute successfully:

1. **Execute All Local JVM Unit Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

2. **Verify Roborazzi Screenshot Visuals**:
   ```bash
   gradle :app:verifyRoborazziDebug
   ```

3. **Record New Baseline Visual Screenshots**:
   ```bash
   gradle :app:recordRoborazziDebug
   ```
