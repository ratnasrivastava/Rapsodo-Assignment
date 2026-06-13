# Architecture & Design Decisions

This document explains how the Golf Performance Tracker is structured and the
reasoning behind the main technical choices.

# Overview

The app follows **MVVM with the Repository pattern**, organized into Clean
Architecture layers. It is a single Gradle module, but the package structure is
deliberately split along layer boundaries (`domain`, `data`, `presentation`) so
the separation of concerns is explicit and the project could be split into
modules later with little churn.

The dependency rule points inward: presentation depends on domain, data depends
on domain, and domain depends on nothing Android-specific.

`
presentation ─┐
              ├──> domain  <── data
        (UI)  ┘  (contracts)  (implementations)
`

# Layers

### Domain (pure Kotlin)

Contains the business model and contracts, with no Android, Room, or Retrofit
imports:

* `Player`, `Shot` — domain models with non-null fields.
* `Resource<T>` — a wrapper (`Loading` / `Success` / `Error`) where every state
  can carry data. This is what makes offline-first possible: the UI can show
  cached content while refreshing, or after a failed refresh.
* `GolfRepository` — the data contract the rest of the app depends on.
* Use cases (`GetPlayersUseCase`, `RefreshPlayersUseCase`, `GetPlayerUseCase`,
  `GetShotsForPlayerUseCase`) — single units of business logic.

### Data

Implements the domain contract:

* **Network**: a Retrofit `GolfApiService` and Moshi DTOs. DTO fields are
  nullable and defensive; mappers coerce them to safe domain defaults so the
  rest of the app never null-checks basic data.
* **Local**: Room entities, DAOs, and the database. DAOs expose `Flow` for reads
  and `suspend` for writes, plus a `PagingSource` query for the paged list.
* **Repository**: `GolfRepositoryImpl` ties network and local together with an
  offline-first strategy (below).

### Presentation

MVVM UI, in both Views and Compose:

* ViewModels expose immutable state (`StateFlow` / `PagingData`) and never hold
  Android `Context`.
* Fragments/Composables render state and forward user intent; they contain no
  business logic.

## Key design decisions

### Single source of truth (offline-first)

The UI only ever observes Room. The repository writes fresh network data into
Room, and because the DAOs return `Flow`/`PagingSource`, the UI updates
automatically. The network is used solely to refresh the cache. This removes a
whole class of cache-coherence bugs and makes offline support a natural property
rather than a special case.

For the players list (paginated), reading and refreshing are split into two
methods: `getPlayersPaged()` observes Room, and `refreshPlayers()` fills Room
from the network. Paging picks up refreshed data automatically because it
observes the database.

### `Resource<T>` carrying data in every state

`Loading` and `Error` can both carry the last known data. The UI therefore shows
cached players/shots while a refresh is in flight or after a failed refresh, and
never presents a blank screen when offline.

### Business logic in use cases

Search and club filtering live in use cases (and, for paging, in the DAO's SQL
query), not in the ViewModel or repository. This keeps each layer focused and
makes the rules independently testable.

### Injected dispatchers

The repository depends on a `DispatcherProvider` abstraction rather than
referencing `Dispatchers` directly. Tests inject a test dispatcher so coroutine
code runs deterministically.

### Reactive search

The search query is a `StateFlow`. It is debounced and mapped with
`flatMapLatest`, so each new query cancels the previous one — the standard
pattern for search-as-you-type. With Paging, filtering is pushed into the SQL
`LIKE` query because a `PagingData` stream cannot be filtered in memory.

### Single immutable UI state per screen (Views)

Each Views screen renders exactly one state object, eliminating impossible
combinations such as "loading and error at once". The paged list instead derives
its loading/empty/error UI from Paging's `LoadState`.

### Semantic theming

All layouts use Material color-role attributes (`?attr/colorPrimary`, etc.)
rather than hardcoded colors, so light and dark themes work everywhere from a
single `values-night` override. The Compose theme mirrors the same palette.

### Pagination choice (Room-backed Paging 3)

The dataset is small and fully cached, so the app pages from Room rather than the
network. This is genuine Paging 3 that fits the offline-first design; a
`RemoteMediator` for network paging would be the next step for a large,
server-paged dataset but would be over-engineering here.

### Custom visualization

The shot chart is a `Canvas`-based custom `View` rather than a charting library,
keeping dependencies minimal and giving full control over the rendering. It
resolves colors from the Material theme, so it adapts to light/dark
automatically.

### UI-agnostic architecture (Compose bonus)

The Compose players screen reuses the exact same `PlayersViewModel`, use cases,
and Paging stream as the Views screen. Only the rendering layer differs, which
demonstrates that the architecture is independent of the UI toolkit. The Compose
screen is split into a stateful wrapper (acquires the ViewModel and paged data)
and a stateless content composable (a pure function of its inputs), so the
content is straightforward to unit-test with controlled data.

## Testing strategy

Tests target the layers where logic lives:

* **Mappers** — null/blank coercion and round-trip symmetry.
* **Use case** — search filtering by name and club.
* **Repository** — offline-first behavior: fetch when empty/forced, skip when
  warm, error handling, and the paged read.
* **ViewModel** — query state and refresh triggering.
* **Compose UI** — rendering, empty state, row click, and search input
  (instrumented).

Flow emissions are asserted with Turbine; `PagingData` is verified with
`asSnapshot()`; coroutine timing is controlled with injected test dispatchers.

## Trade-offs and alternatives considered

* **Single module vs. multi-module.** Kept a single module for simplicity; the
  package layout already enforces the layer boundaries.
* **Views vs. Compose as primary.** Views is primary because the brief specifies
  DataBinding and Material Components; Compose is included for a screen as a bonus.
* **Hand-written fakes vs. mocks.** Fakes are used for the repository in
  use-case/ViewModel tests (readability); MockK is used for DAOs/API in the
  repository test (interaction verification).