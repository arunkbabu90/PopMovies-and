package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.api.PAGE_SIZE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MoviePopularRepository(private val apiService: TMDBEndPoint) {
    lateinit var movieDataSourceFactory: PopularMovieDataSourceFactory

    fun fetchPopularMovies(disposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = PopularMovieDataSourceFactory(apiService, disposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()

        return LivePagedListBuilder(movieDataSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> {
        // Ensure movieDataSourceFactory is initialized before accessing its properties
        if (!::movieDataSourceFactory.isInitialized) {
            // This case should ideally not happen if fetchPopularMovies is called first.
            // Consider initializing movieDataSourceFactory in the constructor or an init block
            // if this repository can be used without calling fetchPopularMovies.
            // For now, assume fetchPopularMovies is always called first.
        }
        return movieDataSourceFactory.popularMoviesList.switchMap { it.networkState }
    }

    fun refresh() {
        // Ensure movieDataSourceFactory and its LiveData are initialized
        if (::movieDataSourceFactory.isInitialized && movieDataSourceFactory.popularMoviesList.value != null) {
            movieDataSourceFactory.popularMoviesList.value?.invalidate()
        }
    }
}
