package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import arunkbabu90.popmovies.data.api.FIRST_PAGE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PopularMovieDataSource(private val apiService: TMDBEndPoint,
                             private val disposable: CompositeDisposable)
    : PageKeyedDataSource<Int, Movie>() {

    private val _networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val TAG = PopularMovieDataSource::class.java.simpleName

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getPopularMovies(FIRST_PAGE)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieResponse ->
                        callback.onResult(movieResponse.movies, null, FIRST_PAGE + 1)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    })
        )
    }
    
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) { }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getPopularMovies(params.key)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { movieResponse ->
                        val nextPageKey = params.key + 1
                        if (movieResponse.totalPages >= nextPageKey) {
                            // Not in last page
                            callback.onResult(movieResponse.movies, params.key + 1)
                            _networkState.postValue(NetworkState.LOADED)
                        } else {
                            // Last page
                            _networkState.postValue(NetworkState.EOL)
                        }
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "Message = null")
                    }
                )
        )
    }
}