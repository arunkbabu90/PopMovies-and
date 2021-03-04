package arunkbabu90.popmovies.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import arunkbabu90.popmovies.data.api.PAGE_SIZE
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Review
import io.reactivex.disposables.CompositeDisposable

class ReviewRepository(private val apiService: TMDBEndPoint) {
    private lateinit var reviewDataSourceFactory: ReviewDataSourceFactory

    fun fetchReviews(disposable: CompositeDisposable, movieId: Int): LiveData<PagedList<Review>> {
        reviewDataSourceFactory = ReviewDataSourceFactory(apiService, disposable, movieId)

        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()

        return LivePagedListBuilder(reviewDataSourceFactory, config).build()
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap(reviewDataSourceFactory.reviewList, ReviewDataSource::networkState)
    }
}