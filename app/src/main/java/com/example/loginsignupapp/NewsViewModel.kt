package com.example.loginsignupapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class NewsViewModel(private val context: Context) : ViewModel() {
    private val _newsItems = MutableLiveData<List<NewsItem>>()
    val newsItems: LiveData<List<NewsItem>> = _newsItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var allNews = mutableListOf<NewsItem>()
    private var displayedCount = 0
    private val LOAD_COUNT = 30

    // API keys
    private val apiKeys = mapOf(
        "alphaVantage" to "WDHLLLWJ99JABKTQ",
        "polygon" to "kD9kVIqD9R4epUl3QgcYMvR5kniOhiim",
        "fmp" to "HeRVjAS9fWH6J3rtMrl7LtXGQL8WNyUK",
        "twelveData" to "a4114485f79149efb45928e74a0b089c",
        "nasdaq" to "XZ9bZGcbS9HotaSLCDXY"
    )

    init {
        refreshNews()
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""

            // Check for internet connection
            if (!isNetworkAvailable()) {
                _errorMessage.value = "No internet connection. Please check your network settings."
                _isLoading.value = false
                return@launch
            }

            try {
                // Fetch news from all APIs in parallel
                val deferredNews = listOf(
                    async { fetchAlphaVantageNews() },
                    async { fetchPolygonNews() },
                    async { fetchFmpNews() },
                    async { fetchTwelveDataNews() },
                    async { fetchNasdaqNews() }
                )

                val newsLists = deferredNews.awaitAll()

                // Combine all news
                allNews.clear()
                newsLists.forEach { newsList ->
                    allNews.addAll(newsList)
                }

                // If no news was fetched, show an error
                if (allNews.isEmpty()) {
                    _errorMessage.value = "No news available. Please try again later."
                    _isLoading.value = false
                    return@launch
                }

                // Remove duplicates based on title
                allNews = allNews.distinctBy { it.title }.toMutableList()

                // Sort by date (newest first)
                allNews.sortByDescending { it.time }

                // Mark the first item as featured
                if (allNews.isNotEmpty()) {
                    allNews[0] = allNews[0].copy(isFeatured = true)
                }

                displayedCount = 0
                loadMoreNews()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch news: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    fun loadMoreNews() {
        if (displayedCount >= allNews.size) {
            _errorMessage.value = "No more news available"
            return
        }

        val endIndex = kotlin.math.min(displayedCount + LOAD_COUNT, allNews.size)
        val newItems = allNews.subList(displayedCount, endIndex)
        val currentList = _newsItems.value?.toMutableList() ?: mutableListOf()
        currentList.addAll(newItems)

        _newsItems.value = currentList
        displayedCount = endIndex
    }

    fun getTotalNewsCount(): Int = allNews.size

    // API fetch methods
    private suspend fun fetchAlphaVantageNews(): List<NewsItem> {
        return try {
            val response = ApiClient.alphaVantageApi.getNews(apiKeys["alphaVantage"] ?: "")
            response.feed?.map { item ->
                NewsItem(
                    title = item.title,
                    summary = item.summary,
                    url = item.url,
                    time = item.time_published
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchPolygonNews(): List<NewsItem> {
        return try {
            val response = ApiClient.polygonApi.getNews(apiKeys["polygon"] ?: "")
            response.results?.map { item ->
                NewsItem(
                    title = item.title,
                    summary = item.description,
                    url = item.url,
                    time = item.published_utc
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchFmpNews(): List<NewsItem> {
        return try {
            val response = ApiClient.fmpApi.getNews(apiKeys["fmp"] ?: "")
            response.data?.map { item ->
                NewsItem(
                    title = item.title,
                    summary = item.text,
                    url = item.url,
                    time = item.publishedDate
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchTwelveDataNews(): List<NewsItem> {
        return try {
            val symbols = listOf("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META")
            val newsItems = mutableListOf<NewsItem>()

            symbols.forEach { symbol ->
                try {
                    val response = ApiClient.twelveDataApi.getStockData(
                        symbol = symbol,
                        interval = "1day",
                        apiKey = apiKeys["twelveData"] ?: ""
                    )

                    response.values?.firstOrNull()?.let { value ->
                        newsItems.add(
                            NewsItem(
                                title = "$symbol Market Update",
                                summary = "$symbol closed at $${value.close} with a change of ${value.percent_change}%",
                                url = "https://twelvedata.com/symbols/$symbol",
                                time = value.datetime
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Skip this symbol and continue
                }
            }

            newsItems
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchNasdaqNews(): List<NewsItem> {
        return try {
            val response = ApiClient.nasdaqApi.getNews(apiKeys["nasdaq"] ?: "")
            response.data?.map { item ->
                NewsItem(
                    title = item.title,
                    summary = item.summary,
                    url = item.url,
                    time = item.published_at
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}