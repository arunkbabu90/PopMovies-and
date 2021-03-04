package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.CastCrewResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CastCrewDataSource(private val apiService: TMDBEndPoint,
                         private val disposable: CompositeDisposable) {

    private val TAG = CastCrewDataSource::class.java.simpleName

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _fetchedCastAndCrew = MutableLiveData<CastCrewResponse>()
    val fetchedCastAndCrew: LiveData<CastCrewResponse>
        get() = _fetchedCastAndCrew

    fun fetchCastAndCrew(movieId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getCastCrew(movieId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { crewCastResponse ->
                        _networkState.postValue(NetworkState.LOADED)
                        _fetchedCastAndCrew.postValue(crewCastResponse)
                    },
                    { e ->
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }
}