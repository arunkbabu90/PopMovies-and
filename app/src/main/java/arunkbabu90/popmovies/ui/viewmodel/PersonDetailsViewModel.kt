package arunkbabu90.popmovies.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arunkbabu90.popmovies.data.model.PersonDetails
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.data.repository.PersonDetailsRepository
import io.reactivex.disposables.CompositeDisposable

class PersonDetailsViewModel(private val repository: PersonDetailsRepository,
                             personId: Int
) : ViewModel() {
    private val disposable = CompositeDisposable()

    val personDetails: LiveData<PersonDetails> by lazy {
        repository.fetchMovieDetails(disposable, personId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.networkState()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}