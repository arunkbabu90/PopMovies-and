package arunkbabu90.popmovies.data.api

import arunkbabu90.popmovies.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.themoviedb.org/3/"
const val POSTER_BASE_URL = "https://image.tmdb.org/t/p"
const val YOUTUBE_THUMB_BASE_URL = "https://img.youtube.com/vi/"
const val YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v="
const val IMG_SIZE_MID = "/w342"
const val IMG_SIZE_LARGE = "/w780"
const val IMG_SIZE_ORIGINAL = "/original"
const val YT_IMG_SIZE_SMALL = "/3.jpg"
const val YT_IMG_SIZE_MID = "/2.jpg"
const val YT_IMG_SIZE_LARGE = "/1.jpg"
const val YT_IMG_SIZE_ORIGINAL = "/0.jpg"

const val FIRST_PAGE = 1
const val PAGE_SIZE = 20

object TMDBClient {
    fun getClient(): TMDBEndPoint {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Options: NONE, BASIC, HEADERS, BODY
        }

        val authorizationInterceptor = Interceptor { chain ->
            val url = chain.request()
                .url
                .newBuilder()
                .build()

            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val retryInterceptor = Interceptor { chain ->
            val request = chain.request()
            var response: Response? = null
            var exception: IOException? = null
            var tryCount = 0
            val maxRetry = 3 // Retry up to 3 times

            while (tryCount < maxRetry && (response == null || !response.isSuccessful)) {
                // Previous response must be closed before retrying
                response?.close()
                try {
                    response = chain.proceed(request)
                    if (response.isSuccessful) {
                        return@Interceptor response
                    }
                } catch (e: IOException) {
                    exception = e
                    Thread.sleep(3000)
                }
                tryCount++
            }

            // If the request was not successful after all retries, return the last response or throw the last exception
            return@Interceptor response ?: throw exception ?: IOException("Retry failed after $maxRetry attempts")
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(retryInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS) // Added read timeout for better resilience
            .writeTimeout(60, TimeUnit.SECONDS) // Added write timeout
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TMDBEndPoint::class.java)
    }
}