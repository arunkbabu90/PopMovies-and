package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.api.PAGE_SIZE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieSearchRepository(private val apiService: TMDBEndPoint) {
    private lateinit var searchDataSourceFactory: SearchDataSourceFactory

    fun fetchSearchResults(disposable: CompositeDisposable, searchTerm: String): LiveData<PagedList<Movie>> {
        searchDataSourceFactory = SearchDataSourceFactory(apiService, disposable, searchTerm)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()

        return LivePagedListBuilder(searchDataSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState>
            = Transformations.switchMap(searchDataSourceFactory.movieList, SearchDataSource::networkState)
}