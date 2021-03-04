package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class SearchDataSourceFactory(private val apiService: TMDBEndPoint,
                              private val disposable: CompositeDisposable,
                              private val searchTerm: String)
    : DataSource.Factory<Int, Movie>() {

    val movieList = MutableLiveData<SearchDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = SearchDataSource(apiService, disposable, searchTerm)
        movieList.postValue(dataSource)

        return dataSource
    }
}