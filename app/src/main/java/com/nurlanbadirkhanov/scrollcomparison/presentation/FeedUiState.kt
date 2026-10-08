package com.nurlanbadirkhanov.scrollcomparison.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class FeedUiState(val articles: List<ArticleUiModel> = emptyList())

@Immutable
data class ArticleUiModel(
    val id: Long,
    val title: String,
    val summary: String,
    val likes: Int
)

enum class ListImplementation { Compose, RecyclerView }
