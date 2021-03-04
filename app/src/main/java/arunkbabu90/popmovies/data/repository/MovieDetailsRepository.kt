package arunkbabu90.popmovies.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.database.MovieDatabase
import arunkbabu90.popmovies.data.model.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: TMDBEndPoint, context: Context) {
    private lateinit var movieDetailsDataSource: MovieDetailsDataSource
    private var db: MovieDatabase = MovieDatabase.getInstance(context)

    fun fetchMovieDetails(disposable: CompositeDisposable, movieId: Int): LiveData<MovieDetails> {
        movieDetailsDataSource = MovieDetailsDataSource(apiService, disposable)
        movieDetailsDataSource.fetchMovieDetails(movieId)

        return movieDetailsDataSource.fetchedMovieDetails
    }

    fun getNetworkState(): LiveData<NetworkState> = movieDetailsDataSource.networkState
}