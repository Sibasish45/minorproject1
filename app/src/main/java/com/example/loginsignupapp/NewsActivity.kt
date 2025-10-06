package com.example.loginsignupapp

import NewsAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewsActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter
    private lateinit var fabScrollTop: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // Initialize ViewModel
        val factory = NewsViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupButtons()
        setupScrollToTop()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter()
        findViewById<RecyclerView>(R.id.news_recycler_view).apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = this@NewsActivity.adapter

            // Add item spacing
            addItemDecoration(SpaceItemDecoration(16))

            // Show FAB when scrolled down
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleItemPosition > 3) fabScrollTop.show()
                    else fabScrollTop.hide()
                }
            })
        }
    }

    private fun setupObservers() {
        viewModel.newsItems.observe(this) { newsItems ->
            adapter.updateNews(newsItems)
            updateNewsCount(newsItems.size)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            findViewById<ProgressBar>(R.id.progress_bar).visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            findViewById<TextView>(R.id.error_text).apply {
                text = errorMessage
                visibility = if (errorMessage.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.refresh_button).setOnClickListener {
            viewModel.refreshNews()
        }

        findViewById<Button>(R.id.load_more_button).setOnClickListener {
            viewModel.loadMoreNews()
        }
    }

    private fun setupScrollToTop() {
        fabScrollTop = findViewById(R.id.fab_scroll_top)
        fabScrollTop.setOnClickListener {
            findViewById<RecyclerView>(R.id.news_recycler_view).smoothScrollToPosition(0)
        }
    }

    private fun updateNewsCount(displayedCount: Int) {
        findViewById<TextView>(R.id.news_count).text =
            "Showing $displayedCount of ${viewModel.getTotalNewsCount()} articles"
    }
}
