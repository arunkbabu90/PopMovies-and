package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Review
import io.reactivex.disposables.CompositeDisposable

class ReviewDataSourceFactory(private val apiService: TMDBEndPoint,
                              private val disposable: CompositeDisposable,
                              private val movieId: Int)
    : DataSource.Factory<Int, Review>() {

    val reviewList = MutableLiveData<ReviewDataSource>()

    override fun create(): DataSource<Int, Review> {
        val dataSource = ReviewDataSource(apiService, disposable, movieId)
        reviewList.postValue(dataSource)

        return dataSource
    }
}