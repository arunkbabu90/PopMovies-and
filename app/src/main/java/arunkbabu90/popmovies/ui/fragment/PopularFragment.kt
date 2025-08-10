package arunkbabu90.popmovies.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.calculateNoOfColumns
import arunkbabu90.popmovies.data.api.TMDBClient
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.MoviePopularRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.databinding.FragmentMoviesListBinding
import arunkbabu90.popmovies.getShortDate
import arunkbabu90.popmovies.isNetworkConnected
import arunkbabu90.popmovies.ui.activity.MovieActivity
import arunkbabu90.popmovies.ui.activity.MovieDetailsActivity
import arunkbabu90.popmovies.ui.adapter.MovieAdapter
import arunkbabu90.popmovies.ui.viewmodel.PopularMovieViewModel

class PopularFragment : Fragment() {
    private var _binding: FragmentMoviesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MoviePopularRepository
    private lateinit var viewModel: PopularMovieViewModel
    private var adapter: MovieAdapter? = null
    private var isLoaded: Boolean = false

    private val TAG = PopularFragment::class.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMoviesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService: TMDBEndPoint = TMDBClient.getClient()
        repository = MoviePopularRepository(apiService)
        viewModel = getViewModel()

        val noOfCols: Int = calculateNoOfColumns(context)

        val lm = GridLayoutManager(context, noOfCols)
        adapter = MovieAdapter { movie, posterView -> if (movie != null) onMovieClick(movie, posterView) }
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter?.getItemViewType(position)
                return if (viewType == adapter?.VIEW_TYPE_MOVIE) 1 else noOfCols
            }
        }
        binding.rvMovieList.setHasFixedSize(true)
        binding.rvMovieList.layoutManager = lm
        binding.rvMovieList.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            isLoaded = false // Reset loaded flag to allow reloading
            loadMovies()
        }

        if (isNetworkConnected(context)) {
            loadMovies(true)
        } else {
            binding.tvErr.text = getString(R.string.err_no_internet)
            binding.tvErr.visibility = View.VISIBLE
        }

        (activity as MovieActivity).networkChangeLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                if (!isLoaded || binding.swipeRefreshLayout.isRefreshing) {
                    loadMovies(true)
                }
            } else {
                binding.tvErr.text = getString(R.string.err_no_internet)
                binding.tvErr.visibility = View.VISIBLE
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun onMovieClick(movie: Movie, posterView: View?) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        val transitionOptions: ActivityOptionsCompat? = if (posterView != null) {
            activity?.let { activity ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    posterView,
                    ViewCompat.getTransitionName(posterView) ?: "poster_transition"
                )
            }
        } else null

        intent.putExtra(MovieDetailsActivity.KEY_MOVIE_ID_EXTRA, movie.movieId)
        intent.putExtra(MovieDetailsActivity.KEY_POSTER_PATH_EXTRA, movie.posterPath)
        intent.putExtra(MovieDetailsActivity.KEY_BACKDROP_PATH_EXTRA, movie.backdropPath)
        intent.putExtra(MovieDetailsActivity.KEY_RATING_EXTRA, movie.rating)
        intent.putExtra(MovieDetailsActivity.KEY_OVERVIEW_EXTRA, movie.overview)
        intent.putExtra(MovieDetailsActivity.KEY_RELEASE_DATE_EXTRA, movie.date.getShortDate())
        intent.putExtra(MovieDetailsActivity.KEY_TITLE_EXTRA, movie.title)

        if (transitionOptions != null)
            startActivity(intent, transitionOptions.toBundle())
        else
            startActivity(intent)
    }

    private fun loadMovies(isShowLoadingIndicator: Boolean = false) {
        if (isLoaded && !binding.swipeRefreshLayout.isRefreshing) {
            return
        }
        binding.swipeRefreshLayout.isRefreshing = true
        if (isShowLoadingIndicator) {
            binding.tvErr.text = getString(R.string.loading)
            binding.tvErr.isVisible = true
        }

        viewModel.popularMovies.observe(viewLifecycleOwner) { moviePagedList ->
            adapter?.submitList(moviePagedList)
            isLoaded = true
            Log.d(TAG, "isLoaded = true")
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.networkState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                if (swipeRefreshLayout.isRefreshing && state != NetworkState.LOADING) {
                    swipeRefreshLayout.isRefreshing = false
                }

                if (viewModel.isEmpty() && state == NetworkState.ERROR) {
                    tvErr.visibility = View.VISIBLE
                    tvErr.text = getString(R.string.err_loading_movies)
                } else if (state == NetworkState.LOADED || !viewModel.isEmpty()) {
                    tvErr.visibility = View.GONE
                }

                if (!swipeRefreshLayout.isRefreshing) {
                    if (state == NetworkState.LOADING && !viewModel.isEmpty()) {
                        adapter?.setNetworkState(state)
                    } else if (state != NetworkState.LOADING) {
                        adapter?.setNetworkState(state)
                    }
                }
            }
        }
    }

    private fun getViewModel(): PopularMovieViewModel {
        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = PopularMovieViewModel(repository) as T
            })[PopularMovieViewModel::class.java]
        }
        return viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
