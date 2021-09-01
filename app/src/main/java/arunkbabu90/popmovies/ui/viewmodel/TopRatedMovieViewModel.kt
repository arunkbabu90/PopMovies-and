package arunkbabu90.popmovies.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.MovieTopRatedRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class TopRatedMovieViewModel(private val repository: MovieTopRatedRepository) : ViewModel() {
    private val disposable = CompositeDisposable()

    val topRatedMovies: LiveData<PagedList<Movie>> by lazy {
        repository.fetchTopRatedMovies(disposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    fun isEmpty() = topRatedMovies.value?.isEmpty() ?: true

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}