package arunkbabu90.popmovies.data.model

import androidx.lifecycle.LiveData
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.PAGE_SIZE
import com.google.firebase.firestore.*

class FavouritesLiveData(private var query: Query,
                         private var onLastVisibleMovieCallback: OnLastVisibleMovieCallback,
                         private var onLastMovieReachedCallback: OnLastMovieReachedCallback)
    : LiveData<Operation>(), EventListener<QuerySnapshot> {

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onActive() {
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null || value == null) return

        for (dc in value.documentChanges) {
            when(dc.type) {
                DocumentChange.Type.ADDED -> {
                    val movie: Favourite = dc.document.toObject(Favourite::class.java)
                    movie.movieId = dc.document.id
                    val addOperation = Operation(movie, R.string.add_operation)
                    setValue(addOperation)
                }
                DocumentChange.Type.MODIFIED -> {
                    val movie: Favourite = dc.document.toObject(Favourite::class.java)
                    movie.movieId = dc.document.id
                    val modifyOperation = Operation(movie, R.string.modify_operation)
                    setValue(modifyOperation)
                }
                DocumentChange.Type.REMOVED -> {
                    val movie: Favourite = dc.document.toObject(Favourite::class.java)
                    movie.movieId = dc.document.id
                    val removeOperation = Operation(movie, R.string.remove_operation)
                    setValue(removeOperation)
                }
            }
        }

        val querySnapshotSize = value.size()
        if (querySnapshotSize < PAGE_SIZE) {
            onLastMovieReachedCallback.setLastMovieReached(true)
        } else {
            val lastVisibleMovie = value.documents[querySnapshotSize - 1]
            onLastVisibleMovieCallback.setLastVisibleProduct(lastVisibleMovie)
        }
    }

    /**
     * Remove the Real-Time Event Listener of Firestore
     */
    fun removeEventListener() {
        listenerRegistration.remove()
    }

    interface OnLastVisibleMovieCallback {
        fun setLastVisibleProduct(lastVisibleMovie: DocumentSnapshot)
    }

    interface OnLastMovieReachedCallback {
        fun setLastMovieReached(isLastMovieReached: Boolean)
    }
}