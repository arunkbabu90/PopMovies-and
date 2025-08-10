package arunkbabu90.popmovies.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.MoviePopularRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class PopularMovieViewModel(private val repository: MoviePopularRepository) : ViewModel() {
    private val disposable = CompositeDisposable()

    val popularMovies: LiveData<PagedList<Movie>> = repository.fetchPopularMovies(disposable)

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    fun refreshData() {
        repository.refresh()
    }

    fun isEmpty() = popularMovies.value?.isEmpty() ?: true

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
