package dev.p1oneer.scrollcomparison.domain

import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun observeArticles(): Flow<List<Article>>
}
