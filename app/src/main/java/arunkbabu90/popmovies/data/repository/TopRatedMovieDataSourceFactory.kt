package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.disposables.CompositeDisposable

class TopRatedMovieDataSourceFactory(private val apiService: TMDBEndPoint,
                                     private val disposable: CompositeDisposable)
    : DataSource.Factory<Int, Movie>() {

    val topRatedMovieList = MutableLiveData<TopRatedMovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val dataSource = TopRatedMovieDataSource(apiService, disposable)
        topRatedMovieList.postValue(dataSource)

        return dataSource
    }
}