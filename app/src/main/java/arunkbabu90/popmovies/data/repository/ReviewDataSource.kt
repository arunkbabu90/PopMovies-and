package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import arunkbabu90.popmovies.data.api.FIRST_PAGE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Review
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ReviewDataSource(private val apiService: TMDBEndPoint,
                       private val disposable: CompositeDisposable,
                       private val movieId: Int)
    : PageKeyedDataSource<Int, Review>() {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val TAG = ReviewDataSource::class.java.simpleName

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Review>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getReviews(movieId, FIRST_PAGE)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { reviewResponse ->
                        _networkState.postValue(NetworkState.LOADED)
                        callback.onResult(reviewResponse.reviews, FIRST_PAGE, FIRST_PAGE + 1)
                    },
                    { e ->
                        Log.e(TAG, e.message ?: "")
                        _networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Review>) { }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Review>) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getReviews(movieId, params.key)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { reviewResponse ->
                        val nextPageKey = params.key + 1
                        if (reviewResponse.totalPages >= nextPageKey) {
                            // Not in last page
                            callback.onResult(reviewResponse.reviews, params.key + 1)
                            _networkState.postValue(NetworkState.LOADED)
                        } else {
                            // Last Page
                            _networkState.postValue(NetworkState.EOL)
                        }
                    },
                    { e ->
                        Log.e(TAG, e.message ?: "")
                        _networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }
}