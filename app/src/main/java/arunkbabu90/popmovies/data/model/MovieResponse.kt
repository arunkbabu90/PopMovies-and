package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(val page: Int,
                         @SerializedName("results") val movies: List<Movie>,
                         @SerializedName("total_pages") val totalPages: Int,
                         @SerializedName("total_results") val totalMovies: Int)