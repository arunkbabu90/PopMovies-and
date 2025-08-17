package arunkbabu90.popmovies.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.popmovies.Constants
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_LARGE
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.api.TMDBClient
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.Company
import arunkbabu90.popmovies.data.model.Person
import arunkbabu90.popmovies.data.model.Video
import arunkbabu90.popmovies.data.repository.CastCrewRepository
import arunkbabu90.popmovies.data.repository.MovieDetailsRepository
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.data.repository.VideoRepository
import arunkbabu90.popmovies.databinding.ActivityMovieDetailsBinding
import arunkbabu90.popmovies.getImageUrl
import arunkbabu90.popmovies.ui.adapter.CastCrewAdapter
import arunkbabu90.popmovies.ui.adapter.CompaniesAdapter
import arunkbabu90.popmovies.ui.adapter.VideoAdapter
import arunkbabu90.popmovies.ui.dialogs.PersonDetailsDialog
import arunkbabu90.popmovies.ui.viewmodel.CastCrewViewModel
import arunkbabu90.popmovies.ui.viewmodel.MovieDetailsViewModel
import arunkbabu90.popmovies.ui.viewmodel.VideoViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var castCrewRepository: CastCrewRepository
    private lateinit var videoRepository: VideoRepository
    private lateinit var posterTarget: CustomTarget<Drawable>
    private lateinit var coverTarget: CustomTarget<Drawable>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var castList = arrayListOf<Person>()
    private var crewList = arrayListOf<Person>()
    private var videoList = arrayListOf<Video>()
    private var movieId = -1
    private var posterPath = ""
    private var coverPath = ""
    private var rating = ""
    private var overview = ""
    private var title = ""
    private var date = ""

    private var isFavourite = false
    private var isFavLoaded = false

    private var prevCrew: Person = Person(name = "", department = "")
    private val TAG = MovieDetailsActivity::class.java.simpleName

    companion object {
        const val KEY_MOVIE_ID_EXTRA = "movieIdExtraKey"
        const val KEY_POSTER_PATH_EXTRA = "posterPathExtraKey"
        const val KEY_BACKDROP_PATH_EXTRA = "backdropPathExtraKey"
        const val KEY_RELEASE_DATE_EXTRA = "releaseDateExtraKey"
        const val KEY_RATING_EXTRA = "ratingExtraKey"
        const val KEY_OVERVIEW_EXTRA = "overviewExtraKey"
        const val KEY_TITLE_EXTRA = "titleExtraKey"

        private const val PERSON_DIALOG_TAG = "personDialogTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        movieId = intent.getIntExtra(KEY_MOVIE_ID_EXTRA, -1)
        posterPath = intent.getStringExtra(KEY_POSTER_PATH_EXTRA) ?: ""
        coverPath = intent.getStringExtra(KEY_BACKDROP_PATH_EXTRA) ?: ""
        rating = intent.getStringExtra(KEY_RATING_EXTRA) ?: ""
        overview = intent.getStringExtra(KEY_OVERVIEW_EXTRA) ?: ""
        date = intent.getStringExtra(KEY_RELEASE_DATE_EXTRA) ?: ""
        title = intent.getStringExtra(KEY_TITLE_EXTRA) ?: ""

        val posterUrl = getImageUrl(posterPath, IMG_SIZE_MID)
        val coverUrl = getImageUrl(coverPath, IMG_SIZE_LARGE)

        // Set enter transition name
        binding.ivMoviePoster.transitionName = movieId.toString()

        // Load available data here for faster loading
        loadPosterAndCover(posterUrl, coverUrl)
        binding.tvMovieTitle.text = title
        binding.tvMovieRating.text = rating
        binding.tvMovieDate.text = date
        // Hide synopsis if is empty
        if (overview.isBlank()) {
            binding.synopsisTitleTextView.visibility = View.GONE
            binding.descriptionCard.visibility = View.GONE
        } else {
            binding.synopsisTitleTextView.visibility = View.VISIBLE
            binding.descriptionCard.visibility = View.VISIBLE
            binding.tvMovieDescription.text = overview
        }

        // Load Favourite Movie Information
        val user = auth.currentUser
        if (user != null) {
            val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"
            db.collection(path).document(movieId.toString())
                .get()
                .addOnSuccessListener { snapshot ->
                    // Success
                    isFavourite = if (snapshot.exists()) {
                        // Favourite Movie
                        binding.fabFavourites.setImageResource(R.drawable.ic_favourite)
                        true
                    } else {
                        // Not added as favourite movie
                        binding.fabFavourites.setImageResource(R.drawable.ic_favourite_outline)
                        false
                    }
                    isFavLoaded = true
                }
        }

        val apiService: TMDBEndPoint = TMDBClient.getClient()
        movieDetailsRepository = MovieDetailsRepository(apiService, this)
        castCrewRepository = CastCrewRepository(apiService)
        videoRepository = VideoRepository(apiService)

        // Movie Details
        val viewModel: MovieDetailsViewModel = getMovieDetailsViewModel(movieId)
        viewModel.movieDetails.observe(this, { movieDetails ->
            // Populate the UI
            setCollapsingToolbarBehaviour(movieDetails.title)
            populateProductionCompanies(movieDetails.companies)
        })

        // Load the cast and crew
        populateCastAndCrew()
        // Load Related Videos
        populateVideos()

        // Restrict Features based on the type of user signed in
        setFeaturesBasedOnUser()

        binding.fabFavourites.setOnClickListener(this)
        binding.actionCardReview.setOnClickListener(this)
        binding.actionCardGlobalChat.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.fabFavourites.id -> {
                if (Constants.isAccountActivated) {
                    addFavMovie()
                } else {
                    Toast.makeText(this, R.string.err_feature_disabled, Toast.LENGTH_SHORT).show()
                }
            }

            binding.actionCardReview.id -> {
                // Open Movie Reviews
                val reviewIntent = Intent(this, ReviewsActivity::class.java)
                reviewIntent.putExtra(ReviewsActivity.REVIEW_MOVIE_ID_EXTRA_KEY, movieId)
                startActivity(reviewIntent)
            }

            binding.actionCardGlobalChat.id -> {
                // Open Movie Global Chat
                if (Constants.userType == Constants.USER_TYPE_PERSON) {
                    // Allow access to chat only to registered Users
                    val chatIntent = Intent(this, ChatActivity::class.java)
                    chatIntent.putExtra(ChatActivity.MOVIE_ID_EXTRA_KEY, movieId.toString())
                    chatIntent.putExtra(ChatActivity.MOVIE_NAME_EXTRA_KEY, title)
                    startActivity(chatIntent)
                } else {
                    // User is a Guest; Don't allow access to the chat
                    Toast.makeText(this, R.string.feature_disabled_for_guest, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Sets the collapsing Toolbar behaviour to show movie title when collapsed and hide title when expanded
     * @param title String The title to show
     */
    private fun setCollapsingToolbarBehaviour(title: String) {
        binding.appBarDetails.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollPos = binding.appBarDetails.totalScrollRange
            val isCollapsed: Boolean = verticalOffset + scrollPos == 0
            binding.movieDetailCollapsingToolbar.title = if (isCollapsed) title else ""
        })
    }

    /**
     * Loads the Poster and Cover to the UI
     * @param posterUrl String The poster URL to load
     * @param coverUrl String The cover URL to load
     */
    private fun loadPosterAndCover(posterUrl: String, coverUrl: String) {
        posterTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.ivMoviePoster.setImageDrawable(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                binding.ivMoviePoster.setImageDrawable(errorDrawable)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                binding.ivMoviePoster.setImageDrawable(null)
            }
        }

        coverTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.ivMovieCover.setImageDrawable(resource)
            }
            override fun onLoadFailed(errorDrawable: Drawable?) {
                binding.ivMovieCover.setImageDrawable(errorDrawable)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                binding.ivMovieCover.setImageDrawable(null)
            }
        }

        Glide.with(this)
            .load(posterUrl)
            .error(R.drawable.ic_img_err)
            .into(posterTarget)

        Glide.with(this)
            .load(coverUrl)
            .error(R.drawable.ic_img_err)
            .into(coverTarget)
    }

    /**
     * Populates the Cast and Crew to the UI
     */
    private fun populateCastAndCrew() {
        // Populate Cast and Crew
        val castAdapter = CastCrewAdapter(true, castList)
        val crewAdapter = CastCrewAdapter(false, crewList)
        with(binding) {
            layoutCast.rvCrew.layoutManager = LinearLayoutManager(
                this@MovieDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            layoutCast.rvCrew.setHasFixedSize(true)
            layoutCast.rvCrew.adapter = crewAdapter

            layoutCast.rvCast.layoutManager = LinearLayoutManager(
                this@MovieDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            layoutCast.rvCast.setHasFixedSize(true)
            layoutCast.rvCast.adapter = castAdapter

            // Cast & Crew Details
            val viewModel: CastCrewViewModel = getCastCrewViewModel(movieId)
            viewModel.castCrewList.observe(this@MovieDetailsActivity, { castCrewResponse ->
                val casts = castCrewResponse.castList

                // Filter crew to avoid redundant persons
                val filteredCrew = castCrewResponse.crewList.filter { crew ->
                    val predicate = (crew.name != prevCrew.name) && (crew.department != prevCrew.department)
                    prevCrew = crew
                    return@filter predicate
                }

                // Cast
                if (casts.isEmpty()) {
                    // Cast list empty so hide the related layout elements
                    layoutCast.rvCast.visibility = View.GONE
                    layoutCast.castTitle.visibility = View.GONE
                } else {
                    castList.addAll(casts)
                    castAdapter.notifyDataSetChanged()
                }

                // Crew
                if (filteredCrew.isEmpty()) {
                    layoutCast.rvCrew.visibility = View.GONE
                    layoutCast.crewTitle.visibility = View.GONE
                } else {
                    crewList.addAll(filteredCrew)
                    crewAdapter.notifyDataSetChanged()
                }
            })

            // Network State
            viewModel.networkState.observe(this@MovieDetailsActivity, { state ->
                if (state == NetworkState.ERROR) {
                    layoutCast.rvCrew.isVisible = false
                    layoutCast.crewTitle.isVisible = false

                    layoutCast.rvCast.isVisible = false
                    layoutCast.castTitle.isVisible = false
                }

                if (state == NetworkState.LOADED) {
                    layoutCast.rvCrew.isVisible = true
                    layoutCast.crewTitle.isVisible = true

                    layoutCast.rvCast.isVisible = true
                    layoutCast.castTitle.isVisible = true
                }

                if (state == NetworkState.LOADING) {
                    layoutCast.rvCrew.isVisible = false
                    layoutCast.crewTitle.isVisible = false

                    layoutCast.rvCast.isVisible = false
                    layoutCast.castTitle.isVisible = false
                }
            })
        }

        castAdapter.itemClickListener = object : CastCrewAdapter.ItemClickListener {
            override fun onPersonClick(position: Int, person: Person) {
                val dialog = PersonDetailsDialog(person, true)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                transaction
                    .add(android.R.id.content, dialog)
                    .addToBackStack(null)
                    .commit()
            }
        }

        crewAdapter.itemClickListener = object : CastCrewAdapter.ItemClickListener {
            override fun onPersonClick(position: Int, person: Person) {
                val dialog = PersonDetailsDialog(person, false)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                transaction
                    .add(android.R.id.content, dialog)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    /**
     * Populates the Production Companies to the UI
     * @param companyList The list of companies to populate
     */
    private fun populateProductionCompanies(companyList: List<Company>) {
        // Populate Production Companies
        if (companyList.isEmpty()) {
            // No companies to show so hide the layout
            binding.layoutCompany.root.visibility = View.GONE
        }
        val lm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.layoutCompany.rvProductionCompany.layoutManager = lm
        binding.layoutCompany.rvProductionCompany.adapter = CompaniesAdapter(companyList)
        binding.layoutCompany.rvProductionCompany.setHasFixedSize(true)
    }

    /**
     * Populates the Related Videos to the UI
     */
    private fun populateVideos() {
        val videoAdapter = VideoAdapter(videoList,
            itemClickListener = { videoUrl -> onVideoClick(videoUrl) },
            itemLongClickListener = { videoUrl -> onVideoLongClick(videoUrl) })

        val lm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.layoutVideos.rvVideos.setHasFixedSize(true)
        binding.layoutVideos.rvVideos.layoutManager = lm
        binding.layoutVideos.rvVideos.adapter = videoAdapter

        val viewModel = getVideoViewModel(movieId)
        viewModel.videoList.observe(this, { videoResponse ->
            // Populate the videos to the adapter
            val videos = videoResponse.videos
            if (videos.isEmpty())
                binding.layoutVideos.root.visibility = View.GONE

            videoList.addAll(videos)
            videoAdapter.notifyDataSetChanged()
        })

        // TODO: Implement Error Checking
        // Network State
        viewModel.networkState.observe(this, { state ->
            if (state == NetworkState.ERROR) {}

            if (state == NetworkState.LOADED) {}

            if (state == NetworkState.LOADING) {}
        })
    }

    /**
     * Invoked when a video is clicked
     */
    private fun onVideoClick(videoUrl: String) {
        // Open an intent to Play the Video using external player
        val playVideoIntent = Intent(Intent.ACTION_VIEW)
        playVideoIntent.data = videoUrl.toUri()
        startActivity(playVideoIntent)
    }

    /**
     * Invoked when a video long pressed
     */
    private fun onVideoLongClick(videoUrl: String) {
        // Open an intent to share the link
        val shareVideoIntent = Intent(Intent.ACTION_SEND)
        shareVideoIntent.putExtra(Intent.EXTRA_TEXT, videoUrl)
        shareVideoIntent.type = "text/plain"

        if (shareVideoIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareVideoIntent, getString(R.string.share_video)))
        } else {
            Toast.makeText(this, getString(R.string.err_no_app_found), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Returns the MovieDetailsViewModel
     */
    private fun getMovieDetailsViewModel(movieId: Int): MovieDetailsViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(movieDetailsRepository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }

    /**
     * Returns the CastCrewViewModel
     */
    private fun getCastCrewViewModel(movieId: Int): CastCrewViewModel {
        return ViewModelProvider(this, object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CastCrewViewModel(castCrewRepository, movieId) as T
            }
        })[CastCrewViewModel::class.java]
    }

    /**
     * Returns the VideoViewModel
     */
    private fun getVideoViewModel(movieId: Int): VideoViewModel {
        return ViewModelProvider(this, object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VideoViewModel(videoRepository, movieId) as T
            }
        })[VideoViewModel::class.java]
    }


    /**
     * Helper method to enable or disable movie details features based on the userType
     */
    private fun setFeaturesBasedOnUser() {
        if (Constants.userType == Constants.USER_TYPE_PERSON) {
            // Normal User
            binding.fabFavourites.show()
        } else {
            // Other user; Guest
            binding.fabFavourites.hide()
        }
    }

    /**
     * Adds the movie as Favourite in Firestore database
     * @return True if operation succeeds, False otherwise
     */
    private fun addFavMovie() {
        val user = auth.currentUser
        if (movieId == -1 || user == null || !isFavLoaded) return

        val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"

        if (isFavourite) {
            // Remove from Favourites
            binding.fabFavourites.setImageResource(R.drawable.ic_favourite_outline)
            db.collection(path)
                .document(movieId.toString())
                .delete()
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, getString(R.string.err_remove_fav), Toast.LENGTH_LONG).show()
                    binding.fabFavourites.setImageResource(R.drawable.ic_favourite)
                }
                .addOnSuccessListener { isFavourite = false }
        } else {
            // Add Movie As Favourite
            binding.fabFavourites.setImageResource(R.drawable.ic_favourite)
            val movie = hashMapOf(
                Constants.FIELD_TITLE to title,
                Constants.FIELD_POSTER_PATH to posterPath,
                Constants.FIELD_BACKDROP_PATH to coverPath,
                Constants.FIELD_RELEASE_DATE to date,
                Constants.FIELD_RATING to rating,
                Constants.FIELD_OVERVIEW to overview,
                Constants.FIELD_TIMESTAMP to Timestamp.now())

            db.collection(path).document(movieId.toString())
                .set(movie, SetOptions.merge())
                .addOnFailureListener { e ->
                    // Failed to add
                    Toast.makeText(applicationContext, getString(R.string.err_add_fav, e), Toast.LENGTH_LONG).show()
                    binding.fabFavourites.setImageResource(R.drawable.ic_favourite_outline)
                }
                .addOnSuccessListener { isFavourite = true }
        }
    }
}