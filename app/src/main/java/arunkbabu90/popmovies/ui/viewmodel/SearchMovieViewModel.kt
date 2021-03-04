package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.MovieSearchRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class SearchMovieViewModel(private val repository: MovieSearchRepository): ViewModel() {
    private val disposable = CompositeDisposable()
    private lateinit var movies: LiveData<PagedList<Movie>>

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    fun searchMovie(searchTerm: String): LiveData<PagedList<Movie>> {
        return lazy {
            movies = repository.fetchSearchResults(disposable, searchTerm)
            movies
        }.value
    }

    fun isEmpty(): Boolean = movies.value?.isEmpty() ?: true

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}