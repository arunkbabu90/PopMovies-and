package arunkbabu90.popmovies.data.repository

import arunkbabu90.filimibeat.ui.viewmodel.FavouritesViewModel
import arunkbabu90.popmovies.Constants
import arunkbabu90.popmovies.data.api.PAGE_SIZE
import arunkbabu90.popmovies.data.model.FavouritesLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MovieFavouriteRepository : FavouritesViewModel.FavouritesRepository,
    FavouritesLiveData.OnLastMovieReachedCallback, FavouritesLiveData.OnLastVisibleMovieCallback {

    private var isLastMovieReached = false
    private var lastVisibleMovie: DocumentSnapshot? = null

    override fun getFavouriteMovies(): FavouritesLiveData? {
        val db: FirebaseFirestore = Firebase.firestore
        val user: FirebaseUser? = Firebase.auth.currentUser

        if (isLastMovieReached || user == null)
            return null

        val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"
        var query = db.collection(path)
            .orderBy(Constants.FIELD_TIMESTAMP, Query.Direction.ASCENDING)
            .limit(PAGE_SIZE.toLong())

        if (lastVisibleMovie != null)
            query = query.startAfter(lastVisibleMovie)

        return FavouritesLiveData(query, this, this)
    }

    override fun setLastVisibleProduct(lastVisibleMovie: DocumentSnapshot) {
        this.lastVisibleMovie = lastVisibleMovie
    }

    override fun setLastMovieReached(isLastMovieReached: Boolean) {
        this.isLastMovieReached = isLastMovieReached
    }
}