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
import arunkbabu90.filimibeat.ui.viewmodel.SearchMovieViewModel
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.calculateNoOfColumns
import arunkbabu90.popmovies.closeSoftInput
import arunkbabu90.popmovies.data.api.TMDBClient
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Movie
import arunkbabu90.popmovies.data.repository.MovieSearchRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.getShortDate
import arunkbabu90.popmovies.ui.activity.MovieDetailsActivity
import arunkbabu90.popmovies.ui.adapter.MovieAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_movie.*
import kotlinx.android.synthetic.main.item_network_state.*

class SearchFragment : Fragment() {
    private lateinit var repository: MovieSearchRepository
    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var adapter: MovieAdapter

    private var lm: GridLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService: TMDBEndPoint = TMDBClient.getClient()
        repository = MovieSearchRepository(apiService)

        val noOfCols = calculateNoOfColumns(context)

        lm = GridLayoutManager(context, noOfCols)
        adapter = MovieAdapter { movie -> if (movie != null) onMovieClick(movie) }
        lm!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return if (viewType == adapter.VIEW_TYPE_MOVIE) 1 else noOfCols
            }
        }
        rv_search_movie_list?.setHasFixedSize(true)
        rv_search_movie_list?.layoutManager = lm
        rv_search_movie_list?.adapter = adapter

        tv_search_err?.visibility = View.VISIBLE
        tv_search_err?.text = getString(R.string.search_for_movie)

        viewModel = getViewModel()

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform search
                if (!query.isNullOrBlank()) {
                    // If there is text in the search field then preform search
                    searchForMovies(query)
                    closeSoftInput(activity)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Search Field is Empty or contains solely of Spaces
                    adapter.setNetworkState(NetworkState.CLEAR)
                    adapter.submitList(null)
                    iv_search_err?.visibility = View.VISIBLE
                    tv_search_err?.visibility = View.VISIBLE
                    iv_search_err?.setImageResource(R.drawable.ic_search)
                    tv_search_err?.text = getString(R.string.search_for_movie)
                }

                return true
            }
        })
    }

    /**
     * Called when a movie item in the grid is clicked
     * @param movie The popular movie
     */
    private fun onMovieClick(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        val posterView = iv_main_poster
        val transitionOptions: ActivityOptionsCompat? =
            activity?.let { activity ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    posterView,
                    ViewCompat.getTransitionName(posterView) ?: "null"
                )
            }

        intent.putExtra(MovieDetailsActivity.KEY_MOVIE_ID_EXTRA, movie.movieId)
        intent.putExtra(MovieDetailsActivity.KEY_POSTER_PATH_EXTRA, movie.posterPath)
        intent.putExtra(MovieDetailsActivity.KEY_BACKDROP_PATH_EXTRA, movie.backdropPath)
        intent.putExtra(MovieDetailsActivity.KEY_RATING_EXTRA, movie.rating)
        intent.putExtra(MovieDetailsActivity.KEY_OVERVIEW_EXTRA, movie.overview)
        intent.putExtra(MovieDetailsActivity.KEY_RELEASE_DATE_EXTRA, movie.date.getShortDate())
        intent.putExtra(MovieDetailsActivity.KEY_TITLE_EXTRA, movie.title)

        if (transitionOptions != null) {
            startActivity(intent, transitionOptions.toBundle())
        }
        else
            startActivity(intent)
    }

    /**
     * Initiate movie search
     * @param searchTerm The term or movie name to search
     */
    private fun searchForMovies(searchTerm: String) {
        viewModel.searchMovie(searchTerm).observe(this, { moviePagedList ->
            adapter.submitList(moviePagedList)
            lm?.scrollToPositionWithOffset(0,0)

            if (moviePagedList.size <= 0) {
                // Movies List Empty; No Movies Found
                tv_search_err?.visibility = View.VISIBLE
                tv_search_err?.text = getString(R.string.no_movies_found)
                iv_search_err?.visibility = View.VISIBLE
                iv_search_err?.setImageResource(R.drawable.ic_frown)
            } else {
                // Movies List NOT Empty; Movies Found
                tv_search_err?.visibility = View.GONE
                iv_search_err?.visibility = View.GONE
            }
        })

        viewModel.networkState.observe(this, { state ->
            item_network_state_progress_bar?.visibility = if (viewModel.isEmpty() && state == NetworkState.LOADING) View.VISIBLE else View.GONE
            item_network_state_err_text_view?.visibility = if (viewModel.isEmpty() && state == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (state == NetworkState.LOADED && !viewModel.isEmpty())
                tv_search_err?.visibility = View.GONE

            if (!viewModel.isEmpty()) {
                adapter.setNetworkState(state)
            }
        })
    }

    private fun getViewModel(): SearchMovieViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = SearchMovieViewModel(repository) as T
        })[SearchMovieViewModel::class.java]
    }
}