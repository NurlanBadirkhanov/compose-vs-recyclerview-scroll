# I Optimized `LazyColumn`, Then Returned to `RecyclerView`

*Why a modern UI stack does not remove the need for engineering judgment—and how I made a critical screen stable without rewriting the entire app.*

Jetpack Compose has long been my default way of building Android UI. Declarative UI, explicit state, and less boilerplate make it a great choice for most screens.

On one of my app's primary screens, though, Compose stopped being just an implementation detail. The feed was a core part of the user experience. During fast scrolling, I started seeing instability: occasional hitches, uneven frame times, and a noticeably less polished feel—especially while the data was updating in the background.

This is not a story about “Compose is bad and RecyclerView is good.” It is about why, after honestly optimizing the Compose implementation, I kept Compose on the screen but brought back `RecyclerView` for one performance-critical list.

## Context: one screen, one data source, two requirements

The home screen loads data from the network and renders a heterogeneous feed: content cards, sections, and loading states. The architecture was not the issue:

```text
RemoteDataSource → Repository → UseCase → ViewModel → StateFlow<FeedUiState> → UI
```

The app followed a clear `data` / `domain` / `presentation` split. The `ViewModel` mapped domain models into a `FeedUiState`, while the UI simply observed it. That boundary mattered: I could replace the list implementation without changing the API layer, repository, use case, or business logic.

The symptoms were in rendering:

- scrolling became less consistent during a sequence of list updates;
- complex cards were expensive enough to challenge the frame budget;
- individual improvements helped locally but did not make the real-world flow reliably predictable.

## What I tried before changing the component

Before replacing the list, I checked the usual sources of unnecessary work in Compose. Important note: in a published article, keep only the items you actually applied and measured in your own app.

### 1. Stable item identity

```kotlin
LazyColumn {
    items(
        items = state.items,
        key = { it.id },
        contentType = { it.type }
    ) { item ->
        FeedItem(item)
    }
}
```

`key` prevents item state from moving to a different row after an update. `contentType` helps Compose reuse work across items with the same structure. Both are essential basics—not a magic button for smooth scrolling.

### 2. Moving calculations out of composition

```kotlin
val visibleItems by remember(state.items, state.filter) {
    derivedStateOf { state.items.filter { it.matches(state.filter) } }
}
```

Anything that did not need to be recalculated during every composition pass was moved out of the composable body. More expensive data preparation belonged in the use case or `ViewModel`.

### 3. Narrowing the update scope

I hoisted state only where it was actually needed, made UI models immutable, and passed the smallest useful set of parameters into each card. That reduced avoidable recompositions and made the UI code easier to reason about.

```kotlin
@Immutable
data class FeedRowUiModel(
    val id: Long,
    val title: String,
    val subtitle: String,
    val imageUrl: String?
)
```

The annotation does not make an app faster by itself; it only communicates type properties to Compose. It is appropriate only when the model is genuinely immutable.

### 4. Optimizing images and nested UI

I verified image sizes, removed unnecessary work from cards, avoided needless animations during scrolling, and constrained expensive nested content. A list problem is often caused by its rows, not only its container.

### 5. Profiling hypotheses instead of trusting intuition

I looked at recompositions and frame timing rather than relying solely on how the screen felt. Layout Inspector, System Trace, JankStats, and Macrobenchmark with `FrameTimingMetric` are useful tools for this.

All of these steps were worthwhile. The screen improved. But on this performance-critical feed, they were not enough: when data changed during active scrolling, I still could not get the consistency I needed.

## The decision: replace the bottleneck, not the whole screen

I kept the rest of the screen in Compose and hosted a `RecyclerView` list through `AndroidView`. The data flow remained exactly the same: the `ViewModel` still publishes `FeedUiState`.

```kotlin
AndroidView(
    factory = { context ->
        RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
            setHasFixedSize(true)
        }
    },
    update = { recyclerView ->
        (recyclerView.adapter as FeedAdapter).submitList(state.items)
    }
)
```

The adapter uses `ListAdapter` and `DiffUtil.ItemCallback`:

```kotlin
class FeedDiffCallback : DiffUtil.ItemCallback<FeedRowUiModel>() {
    override fun areItemsTheSame(oldItem: FeedRowUiModel, newItem: FeedRowUiModel) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FeedRowUiModel, newItem: FeedRowUiModel) = oldItem == newItem
}
```

This made updates explicit and granular: the list receives a new immutable collection, `DiffUtil` calculates changes off the main thread, and `RecyclerView` recycles its views.

## Why it helped in my case

`RecyclerView` did not make the app more “modern.” It made this particular scenario more controllable. For a feed with partial updates, I got predictable recycling, an explicit diff model, and stable behavior that met the requirements of the home screen.

That does not mean `RecyclerView` is always faster than `LazyColumn`. Results depend on row complexity, library versions, device performance, update frequency, and real workload. I did not replace Compose everywhere; it stayed where its benefits outweighed its cost.

## How I would validate the choice fairly

For this article, I am preparing a small public sample with two implementations of the same feed:

- the same UI models, data source, and `ViewModel`;
- the same cards and update scenario;
- `LazyColumn` in one implementation;
- `RecyclerView + ListAdapter + DiffUtil` in the other;
- Macrobenchmark tests that exercise the same fling-scroll and data-refresh flow.

The comparison should run on the same device, using a release build, multiple times. The README should show concrete measurements—such as p50/p90 frame time and slow-frame counts—instead of simply claiming that one approach is “faster.”

## What I learned

1. Measure first, optimize second.
2. Compose optimizations are not a ritual: each one should address a specific source of unnecessary work.
3. You do not have to choose between “all Compose” and “all Views.” A hybrid screen can be a sound architectural decision.
4. A critical user path matters more than technological purity.
5. A reproducible sample and metrics are more valuable than a loud conclusion.

If you have run into a similar issue, I would love to compare scenarios: what did your list render, how frequently did the data change, and which metrics helped you make the call?

---

**Publication note:** Replace the general descriptions of symptoms and outcomes with real data from your project. Do not publish invented FPS figures, percentage improvements, or conclusions that are not backed by profiling.
