package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class PopularMovieDataSourceFactory(private val apiService: TMDBEndPoint,
                                    private val disposable: CompositeDisposable)
    : DataSource.Factory<Int, Movie>() {

    val popularMoviesList = MutableLiveData<PopularMovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = PopularMovieDataSource(apiService, disposable)
        popularMoviesList.postValue(dataSource)
        
        return dataSource
    }
}