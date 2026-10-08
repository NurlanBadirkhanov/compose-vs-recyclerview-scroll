package com.nurlanbadirkhanov.scrollcomparison.domain

import kotlinx.coroutines.flow.Flow

class ObserveFeedUseCase(private val repository: FeedRepository) {
    operator fun invoke(): Flow<List<Article>> = repository.observeArticles()
}
