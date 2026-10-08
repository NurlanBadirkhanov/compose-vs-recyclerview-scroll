package dev.p1oneer.scrollcomparison.data

import dev.p1oneer.scrollcomparison.domain.Article
import dev.p1oneer.scrollcomparison.domain.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultFeedRepository(
    private val remoteDataSource: FeedRemoteDataSource
) : FeedRepository {
    override fun observeArticles(): Flow<List<Article>> =
        remoteDataSource.observeFeed().map { remote ->
            remote.map { it.toDomain() }
        }

    private fun RemoteArticle.toDomain() = Article(id, title, summary, likes)
}
