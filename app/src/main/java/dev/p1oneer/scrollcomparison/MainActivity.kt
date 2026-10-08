package dev.p1oneer.scrollcomparison

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dev.p1oneer.scrollcomparison.presentation.FeedScreen
import dev.p1oneer.scrollcomparison.presentation.FeedViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FeedScreen(viewModel) }
    }
}
