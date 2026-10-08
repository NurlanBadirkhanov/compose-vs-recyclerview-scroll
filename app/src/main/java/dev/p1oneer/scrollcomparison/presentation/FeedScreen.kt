package dev.p1oneer.scrollcomparison.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FeedScreen(viewModel: FeedViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var implementation by remember { mutableStateOf(ListImplementation.Compose) }

    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            Header(
                implementation = implementation,
                onImplementationChanged = { implementation = it }
            )
            when (implementation) {
                ListImplementation.Compose -> ComposeFeed(state.articles)
                ListImplementation.RecyclerView -> RecyclerFeed(state.articles)
            }
        }
    }
}

@Composable
private fun Header(
    implementation: ListImplementation,
    onImplementationChanged: (ListImplementation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text("Same feed. Same state. Different list renderer.", style = MaterialTheme.typography.titleMedium)
        Text(
            "The fake server updates one row every 1.2 seconds.",
            style = MaterialTheme.typography.bodySmall
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
            Button(onClick = { onImplementationChanged(ListImplementation.Compose) }) {
                Text(if (implementation == ListImplementation.Compose) "Compose ✓" else "Compose")
            }
            Button(onClick = { onImplementationChanged(ListImplementation.RecyclerView) }) {
                Text(if (implementation == ListImplementation.RecyclerView) "RecyclerView ✓" else "RecyclerView")
            }
        }
    }
}

@Composable
private fun ComposeFeed(items: List<ArticleUiModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.id },
            contentType = { "article" }
        ) { article ->
            ComposeArticleCard(article)
        }
    }
}

@Composable
private fun ComposeArticleCard(article: ArticleUiModel) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(article.title, style = MaterialTheme.typography.titleMedium)
            Text(article.summary, modifier = Modifier.padding(top = 4.dp))
            Text("${article.likes} likes", modifier = Modifier.padding(top = 10.dp), style = MaterialTheme.typography.labelMedium)
        }
    }
}
