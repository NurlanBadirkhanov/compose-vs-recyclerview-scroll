package com.nurlanbadirkhanov.scrollcomparison.domain

import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun observeArticles(): Flow<List<Article>>
}
