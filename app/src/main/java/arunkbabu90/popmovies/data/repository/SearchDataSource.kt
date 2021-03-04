package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import arunkbabu90.popmovies.data.api.FIRST_PAGE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class SearchDataSource(private val apiService: TMDBEndPoint,
                       private val disposable: CompositeDisposable,
                       private val searchTerm: String)
    : PageKeyedDataSource<Int, Movie>() {

    private val adult = false

    private val _networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val TAG = SearchDataSource::class.java.simpleName

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        if (searchTerm.isBlank()) {
            _networkState.postValue(NetworkState.LOADED)
            return
        }

        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.searchForMovie(searchTerm, FIRST_PAGE, adult)
                .subscribe(
                    { movieResponse ->
                        callback.onResult(movieResponse.movies, null, FIRST_PAGE + 1)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) { }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        if (searchTerm.isBlank()) {
            _networkState.postValue(NetworkState.LOADED)
            return
        }

        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.searchForMovie(searchTerm, params.key, adult)
                .subscribe(
                    { movieResponse ->
                        if (movieResponse.totalPages >= params.key + 1) {
                            // Not in last page
                            callback.onResult(movieResponse.movies, params.key + 1)
                            _networkState.postValue(NetworkState.LOADED)
                        } else {
                            // Last Page
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