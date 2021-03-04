package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.MovieDetails
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDetailsDataSource(private val apiService: TMDBEndPoint,
                             private val disposable: CompositeDisposable) {

    private val TAG = MovieDetailsDataSource::class.java.simpleName

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _fetchedMovieDetails = MutableLiveData<MovieDetails>()
    val fetchedMovieDetails: LiveData<MovieDetails>
        get() = _fetchedMovieDetails

    fun fetchMovieDetails(movieId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getMovieDetails(movieId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieDetails ->
                        _fetchedMovieDetails.postValue(movieDetails)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }
}