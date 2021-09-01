package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.LiveData
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.PersonDetails
import io.reactivex.disposables.CompositeDisposable

class PersonDetailsRepository(private val apiService: TMDBEndPoint) {
    private lateinit var personDetailsDataSource: PersonDetailsDataSource

    fun fetchMovieDetails(disposable: CompositeDisposable, personId: Int): LiveData<PersonDetails> {
        personDetailsDataSource = PersonDetailsDataSource(apiService, disposable)
        personDetailsDataSource.fetchPersonDetails(personId)

        return personDetailsDataSource.fetchedPersonDetails
    }

    fun networkState(): LiveData<NetworkState> = personDetailsDataSource.networkState
}