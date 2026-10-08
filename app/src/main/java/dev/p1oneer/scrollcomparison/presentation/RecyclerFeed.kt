package dev.p1oneer.scrollcomparison.presentation

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@Composable
fun RecyclerFeed(items: List<ArticleUiModel>) {
    val adapter = remember { ArticleAdapter() }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
                setHasFixedSize(true)
                clipToPadding = false
                setPadding(12.dp, 12.dp, 12.dp, 12.dp)
            }
        },
        update = { adapter.submitList(items) }
    )
}

private val Int.dp: Int
    get() = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()

private class ArticleAdapter : ListAdapter<ArticleUiModel, ArticleViewHolder>(ArticleDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val context = parent.context
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp, 16.dp, 16.dp, 16.dp)
            setBackgroundColor(Color.WHITE)
            elevation = 2.dp.toFloat()
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8.dp }
        }
        val title = TextView(context).apply { textSize = 18f; setTextColor(Color.BLACK) }
        val summary = TextView(context).apply { textSize = 14f; setTextColor(Color.DKGRAY); setPadding(0, 6.dp, 0, 0) }
        val likes = TextView(context).apply {
            textSize = 12f
            gravity = Gravity.END
            setTextColor(Color.GRAY)
            setPadding(0, 10.dp, 0, 0)
        }
        card.addView(title)
        card.addView(summary)
        card.addView(likes)
        return ArticleViewHolder(card, title, summary, likes)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) = holder.bind(getItem(position))
}

private class ArticleViewHolder(
    view: LinearLayout,
    private val title: TextView,
    private val summary: TextView,
    private val likes: TextView
) : RecyclerView.ViewHolder(view) {
    fun bind(item: ArticleUiModel) {
        title.text = item.title
        summary.text = item.summary
        likes.text = "${item.likes} likes"
    }
}

private object ArticleDiffCallback : DiffUtil.ItemCallback<ArticleUiModel>() {
    override fun areItemsTheSame(oldItem: ArticleUiModel, newItem: ArticleUiModel) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ArticleUiModel, newItem: ArticleUiModel) = oldItem == newItem
}
