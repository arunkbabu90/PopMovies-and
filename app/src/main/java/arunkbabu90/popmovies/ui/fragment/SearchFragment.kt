package arunkbabu90.popmovies.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.calculateNoOfColumns
import arunkbabu90.popmovies.closeSoftInput
import arunkbabu90.popmovies.data.api.TMDBClient
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.MovieSearchRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.databinding.FragmentSearchBinding
import arunkbabu90.popmovies.getShortDate
import arunkbabu90.popmovies.ui.activity.MovieDetailsActivity
import arunkbabu90.popmovies.ui.adapter.MovieAdapter
import arunkbabu90.popmovies.ui.viewmodel.SearchMovieViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MovieSearchRepository
    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var adapter: MovieAdapter

    private var lm: GridLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService: TMDBEndPoint = TMDBClient.getClient()
        repository = MovieSearchRepository(apiService)

        val noOfCols = calculateNoOfColumns(context)

        lm = GridLayoutManager(context, noOfCols)
        adapter = MovieAdapter { movie, posterView ->
            if (movie != null) onMovieClick(movie, posterView)
        }
        lm?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return if (viewType == adapter.VIEW_TYPE_MOVIE) 1 else noOfCols
            }
        }
        binding.rvSearchMovieList.setHasFixedSize(true)
        binding.rvSearchMovieList.layoutManager = lm
        binding.rvSearchMovieList.adapter = adapter

        binding.tvSearchErr.visibility = View.VISIBLE
        binding.tvSearchErr.text = getString(R.string.search_for_movie)

        viewModel = getViewModel()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchForMovies(query)
                    closeSoftInput(activity)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    adapter.setNetworkState(NetworkState.CLEAR)
                    adapter.submitList(null)
                    binding.ivSearchErr.visibility = View.VISIBLE
                    binding.tvSearchErr.visibility = View.VISIBLE
                    binding.ivSearchErr.setImageResource(R.drawable.ic_search)
                    binding.tvSearchErr.text = getString(R.string.search_for_movie)
                }
                return true
            }
        })
    }

    private fun onMovieClick(movie: Movie, posterView: View?) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)

        // Create shared element transition with the clicked poster
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

        if (transitionOptions != null) {
            startActivity(intent, transitionOptions.toBundle())
        } else {
            startActivity(intent)
        }
    }

    private fun searchForMovies(searchTerm: String) {
        viewModel.searchMovie(searchTerm).observe(viewLifecycleOwner) { moviePagedList ->
            adapter.submitList(moviePagedList)
            lm?.scrollToPositionWithOffset(0,0)

            if (moviePagedList.isEmpty()) {
                binding.tvSearchErr.visibility = View.VISIBLE
                binding.tvSearchErr.text = getString(R.string.no_movies_found)
                binding.ivSearchErr.visibility = View.VISIBLE
                binding.ivSearchErr.setImageResource(R.drawable.ic_frown)
            } else {
                binding.tvSearchErr.visibility = View.GONE
                binding.ivSearchErr.visibility = View.GONE
            }
        }

        viewModel.networkState.observe(viewLifecycleOwner) { state ->
            // Show error in the existing error TextView
            if (viewModel.isEmpty() && state == NetworkState.ERROR) {
                binding.tvSearchErr.visibility = View.VISIBLE
                binding.tvSearchErr.text = getString(R.string.err_loading_movies) // You may need to add this string resource
            }

            if (state == NetworkState.LOADED) {
                binding.tvSearchErr.visibility = View.GONE
            }

            if (state == NetworkState.LOADING && !viewModel.isEmpty()) {
                // Show loading state for pagination
                adapter.setNetworkState(state)
            } else if (state != NetworkState.LOADING) {
                adapter.setNetworkState(state)
            }
        }
    }

    private fun getViewModel(): SearchMovieViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = SearchMovieViewModel(repository) as T
        })[SearchMovieViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
