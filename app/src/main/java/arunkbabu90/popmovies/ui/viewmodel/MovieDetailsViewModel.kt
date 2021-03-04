package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arunkbabu90.popmovies.data.model.MovieDetails
import arunkbabu90.popmovies.data.repository.MovieDetailsRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsViewModel(private val repository: MovieDetailsRepository, movieId: Int) : ViewModel() {
    private val disposable = CompositeDisposable()

    val movieDetails: LiveData<MovieDetails> by lazy {
        repository.fetchMovieDetails(disposable, movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}