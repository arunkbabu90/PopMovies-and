package arunkbabu90.popmovies.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arunkbabu90.popmovies.data.model.CastCrewResponse
import arunkbabu90.popmovies.data.repository.CastCrewRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class CastCrewViewModel(private val repository: CastCrewRepository, movieId: Int) : ViewModel() {
    private val disposable = CompositeDisposable()

    val castCrewList: LiveData<CastCrewResponse> by lazy {
        repository.fetchCastAndCrew(disposable, movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}