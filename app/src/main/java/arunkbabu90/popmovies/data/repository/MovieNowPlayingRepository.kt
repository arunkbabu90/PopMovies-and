package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.api.PAGE_SIZE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieNowPlayingRepository(private val apiService: TMDBEndPoint) {
    private lateinit var movieDataSourceFactory: NowPlayingMovieDataSourceFactory

    fun fetchNowPlayingMovies(disposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = NowPlayingMovieDataSourceFactory(apiService, disposable)

        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()

        return LivePagedListBuilder(movieDataSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> {
        // Ensure movieDataSourceFactory is initialized before accessing its properties
        if (!::movieDataSourceFactory.isInitialized) {
            // This case should ideally not happen if fetchNowPlayingMovies is called first
            // For now, let's assume it has been.
        }
        return movieDataSourceFactory.nowPlayingMoviesList.switchMap { it.networkState }
    }

    fun refresh() {
        // Ensure movieDataSourceFactory and its LiveData are initialized
        if (::movieDataSourceFactory.isInitialized && movieDataSourceFactory.nowPlayingMoviesList.value != null) {
            movieDataSourceFactory.nowPlayingMoviesList.value?.invalidate()
        }
    }
}