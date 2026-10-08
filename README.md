# Compose vs RecyclerView: an update-during-scroll sample

This is a deliberately small Android sample for discussing a practical question:

> When a critical feed needs more predictable behavior during updates, is a hybrid Compose + RecyclerView screen a reasonable choice?

It is **not** a claim that one list implementation is universally faster. It keeps the data flow, UI model, row content, and update cadence the same, then lets you switch only the renderer.

## Companion article

[Why I Replaced LazyColumn With RecyclerView on a Critical Android Screen](https://nurlanbadirkhanov.medium.com/why-i-replaced-lazycolumn-with-recyclerview-on-a-critical-android-screen-969a1b21e11d)

## What is comparable

```text
Fake remote source → Repository → Use case → ViewModel → StateFlow<FeedUiState>
                                                              ↓
                                                   Compose / RecyclerView
```

- The fake remote data source publishes 100 articles.
- Every 1.2 seconds it returns a new immutable list with exactly one changed item.
- Both paths observe the same `FeedUiState`.
- The Compose path uses `LazyColumn` with stable `key` and `contentType`.
- The View path is hosted inside Compose with `AndroidView`, `RecyclerView`, `ListAdapter`, and `DiffUtil`.

## Why this is structured as one app

Two separate apps would make it too easy to compare different state holders, data transformations, or card content. Here the only meaningful variable is how the list is rendered.

## Run

Open the project in Android Studio and run the `app` configuration on a physical device. Switch between **Compose** and **RecyclerView**, then perform the same fling-scroll while the simulated server updates rows.

Use a release-like build and profile on the same physical device. A subjective test is a starting point—not a benchmark.

## Suggested measurement protocol

1. Warm up each implementation with three runs.
2. Run the same scroll gesture at least five times per implementation.
3. Record frame timing with Macrobenchmark / `FrameTimingMetric`, System Trace, or JankStats.
4. Report device model, Android version, build variant, library versions, p50/p90 frame duration, and slow-frame count.
5. Treat results as scenario-specific.

## Project layout

```text
data/           Fake remote source and repository implementation
domain/         Models, repository contract, use case
presentation/   ViewModel, shared UI state, Compose and RecyclerView renderers
```

## Article

The companion Medium draft is in [`article-draft.md`](article-draft.md). Before publishing, replace generic wording with measurements from your own device and app. Never publish invented FPS numbers or percentage improvements.

## License

MIT
