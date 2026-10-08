package com.nurlanbadirkhanov.scrollcomparison

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.nurlanbadirkhanov.scrollcomparison.presentation.FeedScreen
import com.nurlanbadirkhanov.scrollcomparison.presentation.FeedViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FeedScreen(viewModel) }
    }
}
