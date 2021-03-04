package arunkbabu90.popmovies.data.api

import arunkbabu90.popmovies.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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
        val interceptor = Interceptor { chain ->
            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
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