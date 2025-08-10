package arunkbabu90.popmovies.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
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
    private var isLoaded: Boolean = false // Tracks if at least one successful load has occurred

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

        setupRecyclerView()
        setupSwipeToRefresh()
        setupObservers()

        if (isNetworkConnected(context)) {
            if (!isLoaded && viewModel.isEmpty()) {
                 binding.tvErr.text = getString(R.string.loading)
                 binding.tvErr.visibility = View.VISIBLE
            }
            if (viewModel.popularMovies.value == null || binding.swipeRefreshLayout.isRefreshing) {
                viewModel.refreshData()
            }
        } else {
            binding.tvErr.text = getString(R.string.err_no_internet)
            binding.tvErr.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
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
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun setupObservers() {
        viewModel.popularMovies.observe(viewLifecycleOwner) { moviePagedList ->
            adapter?.submitList(moviePagedList)
            if (moviePagedList.isNotEmpty()) {
                isLoaded = true
                binding.tvErr.visibility = View.GONE
            }
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.networkState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefreshLayout.isRefreshing = state == NetworkState.LOADING

            when (state) {
                NetworkState.LOADING -> {
                    if (viewModel.isEmpty() && !isLoaded) {
                        binding.tvErr.text = getString(R.string.loading)
                        binding.tvErr.visibility = View.VISIBLE
                    } else {
                        binding.tvErr.visibility = View.GONE
                    }
                }
                NetworkState.LOADED -> {
                    isLoaded = true
                    if (viewModel.isEmpty()) {
                        binding.tvErr.text = getString(R.string.no_movies_found)
                        binding.tvErr.visibility = View.VISIBLE
                    } else {
                        binding.tvErr.visibility = View.GONE
                    }
                }
                NetworkState.ERROR -> {
                    if (viewModel.isEmpty()) {
                        binding.tvErr.text = getString(R.string.err_loading_movies)
                        binding.tvErr.visibility = View.VISIBLE
                    } else {
                        binding.tvErr.visibility = View.GONE
                    }
                }
                else -> {
                     binding.tvErr.visibility = View.GONE
                }
            }
            if (!viewModel.isEmpty() || state != NetworkState.LOADING) {
                adapter?.setNetworkState(state)
            }
        }

        (activity as MovieActivity).networkChangeLiveData.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                if (view != null && !isLoaded && !binding.swipeRefreshLayout.isRefreshing) {
                     if (viewModel.isEmpty()) {
                        binding.tvErr.text = getString(R.string.loading)
                        binding.tvErr.visibility = View.VISIBLE
                    }
                    viewModel.refreshData()
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

    private fun getViewModel(): PopularMovieViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PopularMovieViewModel(repository) as T
        })[PopularMovieViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvMovieList.adapter = null
        _binding = null
    }
}
