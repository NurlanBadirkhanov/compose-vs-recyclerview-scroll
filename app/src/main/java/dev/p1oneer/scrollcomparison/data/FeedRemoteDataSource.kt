package dev.p1oneer.scrollcomparison.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Simulates server snapshots without coupling the UI to network code. */
class FeedRemoteDataSource {
    fun observeFeed(): Flow<List<RemoteArticle>> = flow {
        var articles = (1..100).map { index ->
            RemoteArticle(
                id = index.toLong(),
                title = "Article #$index",
                summary = "The same data and card complexity are rendered by both list implementations.",
                likes = index * 3
            )
        }
        emit(articles)

        var tick = 0
        while (true) {
            delay(1_200)
            val changedIndex = tick % articles.size
            articles = articles.mapIndexed { index, article ->
                if (index == changedIndex) article.copy(likes = article.likes + 1) else article
            }
            emit(articles)
            tick++
        }
    }
}

data class RemoteArticle(
    val id: Long,
    val title: String,
    val summary: String,
    val likes: Int
)
