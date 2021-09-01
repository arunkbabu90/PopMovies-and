package arunkbabu90.popmovies.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.model.Review
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.data.repository.ReviewRepository
import io.reactivex.disposables.CompositeDisposable

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {
    private val disposable = CompositeDisposable()
    private lateinit var reviews: LiveData<PagedList<Review>>

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    fun fetchReviews(movieId: Int): LiveData<PagedList<Review>> {
        return lazy {
            reviews = repository.fetchReviews(disposable, movieId)
            reviews
        }.value
    }

    fun isEmpty(): Boolean = reviews.value?.isEmpty() ?: true

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}