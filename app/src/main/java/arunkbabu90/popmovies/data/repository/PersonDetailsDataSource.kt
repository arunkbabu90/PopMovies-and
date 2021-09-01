package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.PersonDetails
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PersonDetailsDataSource(private val apiService: TMDBEndPoint,
                              private val disposable: CompositeDisposable) {

    private val TAG = PersonDetailsDataSource::class.java.simpleName

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> get() = _networkState

    private val _fetchedPersonDetails = MutableLiveData<PersonDetails>()
    val fetchedPersonDetails: LiveData<PersonDetails> get() = _fetchedPersonDetails

    fun fetchPersonDetails(personId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getPersonDetails(personId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { personDetails ->
                        // onSuccess
                        _fetchedPersonDetails.postValue(personDetails)
                        _networkState.postValue(NetworkState.LOADED)
                    },
                    { e ->
                        // onFailure
                        _networkState.postValue(NetworkState.ERROR)
                        Log.e(TAG, e.message ?: "")
                    }
                )
        )
    }

}