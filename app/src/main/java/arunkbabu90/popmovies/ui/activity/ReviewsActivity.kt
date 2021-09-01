package arunkbabu90.popmovies.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.TMDBClient
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.data.repository.ReviewRepository
import arunkbabu90.popmovies.databinding.ActivityReviewsBinding
import arunkbabu90.popmovies.ui.adapter.ReviewAdapter
import arunkbabu90.popmovies.ui.viewmodel.ReviewViewModel

class ReviewsActivity : AppCompatActivity() {
    private lateinit var repository: ReviewRepository
    private lateinit var binding: ActivityReviewsBinding

    private var movieId = -1
    private var isLoaded = false
    private var isListEmpty = false

    companion object {
        const val REVIEW_MOVIE_ID_EXTRA_KEY = "reviewMovieIdExtraKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getIntExtra(REVIEW_MOVIE_ID_EXTRA_KEY, -1)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorDarkerGrey)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorDarkBackgroundGrey1)

        val apiService = TMDBClient.getClient()
        repository = ReviewRepository(apiService)

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ReviewAdapter()
        binding.rvReviews.layoutManager = lm
        binding.rvReviews.setHasFixedSize(true)
        binding.rvReviews.adapter = adapter

        val viewModel = getViewModel()
        viewModel.fetchReviews(movieId).observe(this, { reviewPagedList ->
            isListEmpty = reviewPagedList.isEmpty()
            adapter.submitList(reviewPagedList)
        })

        viewModel.networkState.observe(this, { state ->
            binding.pbReviews.visibility =
                if (viewModel.isEmpty() && state == NetworkState.LOADING) View.VISIBLE else View.GONE

            isLoaded = state == NetworkState.LOADED

            if (isLoaded && isListEmpty) {
                binding.tvReviewsErr.visibility = View.VISIBLE
                binding.tvReviewsErr.text = getString(R.string.no_reviews_found)
            } else {
                binding.tvReviewsErr.visibility = View.GONE
            }


            binding.tvReviewsErr.visibility = if (state == NetworkState.ERROR) View.VISIBLE else View.GONE

            binding.tvReviewsErr.visibility = if (viewModel.isEmpty()) View.VISIBLE else View.GONE

            if (!viewModel.isEmpty())
                adapter.setNetworkState(state)
        })
    }

    private fun getViewModel(): ReviewViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReviewViewModel(repository) as T
            }
        })[ReviewViewModel::class.java]
    }
}