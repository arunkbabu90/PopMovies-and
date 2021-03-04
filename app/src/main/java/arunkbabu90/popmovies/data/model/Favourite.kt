package arunkbabu90.popmovies.data.model

import com.google.firebase.Timestamp

data class Favourite(var movieId: String = "",
                     val title: String = "",
                     val posterPath: String = "",
                     val backdropPath: String = "",
                     val overview: String = "",
                     val releaseDate: String = "",
                     val rating: String = "",
                     val timestamp: Timestamp = Timestamp(0,0))