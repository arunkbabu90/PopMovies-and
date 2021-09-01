package arunkbabu90.popmovies.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.popmovies.Constants
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.model.Favourite
import arunkbabu90.popmovies.data.model.FavouritesLiveData
import arunkbabu90.popmovies.ui.activity.MovieDetailsActivity
import arunkbabu90.popmovies.ui.adapter.FavouritesAdapter
import arunkbabu90.popmovies.ui.viewmodel.FavouritesViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.item_favourites.*

class FavouritesFragment : Fragment() {
    private lateinit var adapter: FavouritesAdapter

    private val db: FirebaseFirestore = Firebase.firestore
    private val auth = Firebase.auth
    private val favouriteMovies = arrayListOf<Favourite>()
    private var isScrolling = false
    private var favouritesLiveData: FavouritesLiveData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user: FirebaseUser = auth.currentUser ?: return
        val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"

        adapter = FavouritesAdapter(favouriteMovies) { favouriteMovie ->
            if (favouriteMovie != null) onFavouriteClick(favouriteMovie)
        }
        tv_fav_err.visibility = if (favouriteMovies.isNullOrEmpty()) View.VISIBLE else View.GONE

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_favourites.setHasFixedSize(true)
        rv_favourites.layoutManager = lm
        rv_favourites.adapter = adapter

        rv_favourites.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleMoviePos = lm.findFirstVisibleItemPosition()
                val visibleMovieCount = lm.childCount
                val totalMovieCount = lm.itemCount

                if (isScrolling && (firstVisibleMoviePos + visibleMovieCount == totalMovieCount)) {
                    // End Of Page; So load more movies if available
                    isScrolling = false
                    getMovies()
                }
            }
        })

        getMovies()

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Delete the Favourite Movie from the list (database)
                val holder = viewHolder as FavouritesAdapter.FavouritesViewHolder
                val movie: Favourite = holder.movie ?: return

                db.collection(path)
                    .document(movie.movieId)
                    .delete()
                    .addOnSuccessListener {
                        // Undo Action
                        val snackbar = Snackbar.make(favourites_fragment_layout, "${movie.title} Removed", Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo)) {
                                val favMovie = hashMapOf(
                                    Constants.FIELD_TITLE to movie.title,
                                    Constants.FIELD_POSTER_PATH to movie.posterPath,
                                    Constants.FIELD_BACKDROP_PATH to movie.backdropPath,
                                    Constants.FIELD_OVERVIEW to movie.overview,
                                    Constants.FIELD_RELEASE_DATE to movie.releaseDate,
                                    Constants.FIELD_RATING to movie.rating,
                                    Constants.FIELD_TIMESTAMP to Timestamp.now())

                                db.collection(path).document(movie.movieId)
                                    .set(favMovie, SetOptions.merge())
                            }
                        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                        snackbar.show()
                    }
            }
        }).attachToRecyclerView(rv_favourites)
    }

    /**
     * Called when a Favourite Movie is clicked
     * @param favMovie Favourite  The favourite movie that is clicked
     */
    private fun onFavouriteClick(favMovie: Favourite) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        val posterView = iv_fav_poster
        val transitionOptions: ActivityOptionsCompat? =
            activity?.let { activity ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    posterView,
                    ViewCompat.getTransitionName(posterView) ?: "null"
                )
            }

        intent.putExtra(MovieDetailsActivity.KEY_MOVIE_ID_EXTRA, favMovie.movieId.toInt())
        intent.putExtra(MovieDetailsActivity.KEY_POSTER_PATH_EXTRA, favMovie.posterPath)
        intent.putExtra(MovieDetailsActivity.KEY_BACKDROP_PATH_EXTRA, favMovie.backdropPath)
        intent.putExtra(MovieDetailsActivity.KEY_RATING_EXTRA, favMovie.rating)
        intent.putExtra(MovieDetailsActivity.KEY_OVERVIEW_EXTRA, favMovie.overview)
        intent.putExtra(MovieDetailsActivity.KEY_RELEASE_DATE_EXTRA, favMovie.releaseDate)
        intent.putExtra(MovieDetailsActivity.KEY_TITLE_EXTRA, favMovie.title)

        if (transitionOptions != null)
            startActivity(intent, transitionOptions.toBundle())
        else
            startActivity(intent)
    }

    private fun getViewModel() = ViewModelProvider(this).get(FavouritesViewModel::class.java)

    private fun getMovies() {
        val viewModel = getViewModel()
        favouritesLiveData = viewModel.getFavouritesLiveData() ?: return

        favouritesLiveData?.observe(viewLifecycleOwner) { operation ->
            when (operation.type) {
                R.string.add_operation -> {
                    // Add
                    val addedMovie = operation.favMovie
                    if (!favouriteMovies.contains(addedMovie))
                        favouriteMovies.add(addedMovie)
                }
                R.string.modify_operation -> {
                    // Modify
                    val modifiedMovie = operation.favMovie
                    for ((i, currentMovie) in favouriteMovies.withIndex())
                        if (currentMovie.movieId == modifiedMovie.movieId) {
                            favouriteMovies.remove(currentMovie)
                            favouriteMovies.add(i, modifiedMovie)
                        }
                }
                R.string.remove_operation -> {
                    // Remove
                    val removedMovie = operation.favMovie
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        favouriteMovies.removeIf { it.movieId == removedMovie.movieId }
                    } else {
                        val iterator = favouriteMovies.iterator()
                        while (iterator.hasNext()) {
                            val currMovie = iterator.next()
                            if (currMovie.movieId == removedMovie.movieId)
                                iterator.remove()
                        }
                    }
                }
            }
            tv_fav_err.visibility = if (favouriteMovies.isNullOrEmpty()) View.VISIBLE else View.GONE
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        favouritesLiveData?.removeEventListener()
    }
}