package dev.p1oneer.scrollcomparison.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.p1oneer.scrollcomparison.data.DefaultFeedRepository
import dev.p1oneer.scrollcomparison.data.FeedRemoteDataSource
import dev.p1oneer.scrollcomparison.domain.ObserveFeedUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FeedViewModel : ViewModel() {
    private val observeFeed = ObserveFeedUseCase(
        DefaultFeedRepository(FeedRemoteDataSource())
    )

    val uiState: StateFlow<FeedUiState> = observeFeed()
        .map { articles ->
            FeedUiState(
                articles = articles.map { article ->
                    ArticleUiModel(article.id, article.title, article.summary, article.likes)
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FeedUiState())
}
