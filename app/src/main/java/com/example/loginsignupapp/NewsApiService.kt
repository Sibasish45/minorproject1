package com.example.loginsignupapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Alpha Vantage API
interface AlphaVantageApiService {
    @GET("query?function=NEWS_SENTIMENT")
    suspend fun getNews(@Query("apikey") apiKey: String): AlphaVantageResponse
}

// Polygon API
interface PolygonApiService {
    @GET("v2/reference/news")
    suspend fun getNews(@Query("apiKey") apiKey: String, @Query("limit") limit: Int = 1000): PolygonResponse
}

// Financial Modelling Prep API
interface FmpApiService {
    @GET("api/v3/general_news")
    suspend fun getNews(@Query("apikey") apiKey: String, @Query("limit") limit: Int = 100): FmpResponse
}

// Twelve Data API
interface TwelveDataApiService {
    @GET("time_series")
    suspend fun getStockData(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("apikey") apiKey: String
    ): TwelveDataResponse
}

// Nasdaq Data API
interface NasdaqApiService {
    @GET("api/v3/news")
    suspend fun getNews(@Query("api_key") apiKey: String, @Query("limit") limit: Int = 100): NasdaqResponse
}

// Response data classes
data class AlphaVantageResponse(val feed: List<AlphaVantageNewsItem>?)
data class AlphaVantageNewsItem(
    val title: String,
    val summary: String?,
    val url: String,
    val time_published: String
)

data class PolygonResponse(val results: List<PolygonNewsItem>?)
data class PolygonNewsItem(
    val title: String,
    val description: String?,
    val url: String,
    val published_utc: String
)

data class FmpResponse(val data: List<FmpNewsItem>?)
data class FmpNewsItem(
    val title: String,
    val text: String?,
    val url: String,
    val publishedDate: String
)

data class TwelveDataResponse(val values: List<TwelveDataValue>?)
data class TwelveDataValue(
    val datetime: String,
    val close: String,
    val change: String,
    val percent_change: String,
    val volume: String
)

data class NasdaqResponse(val data: List<NasdaqNewsItem>?)
data class NasdaqNewsItem(
    val title: String,
    val summary: String?,
    val url: String,
    val published_at: String
)

// Retrofit instances for each API
object ApiClient {
    private const val ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/"
    private const val POLYGON_BASE_URL = "https://api.polygon.io/"
    private const val FMP_BASE_URL = "https://financialmodelingprep.com/"
    private const val TWELVE_DATA_BASE_URL = "https://api.twelvedata.com/"
    private const val NASDAQ_BASE_URL = "https://data.nasdaq.com/"

    val alphaVantageApi: AlphaVantageApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ALPHA_VANTAGE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlphaVantageApiService::class.java)
    }

    val polygonApi: PolygonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(POLYGON_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PolygonApiService::class.java)
    }

    val fmpApi: FmpApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FMP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FmpApiService::class.java)
    }

    val twelveDataApi: TwelveDataApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TWELVE_DATA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwelveDataApiService::class.java)
    }

    val nasdaqApi: NasdaqApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NASDAQ_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasdaqApiService::class.java)
    }
}