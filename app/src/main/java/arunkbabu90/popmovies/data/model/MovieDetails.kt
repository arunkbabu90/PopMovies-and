package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class MovieDetails(var movieId: Long,
                        var title: String,
                        var overview: String,
                        var popularity: Double,
                        var budget: Long,
                        var revenue: Long,
                        var runtime: Int,
                        var status: String,
                        var video: Boolean,
                        var genres: List<Genre> = listOf(),
                        @SerializedName("production_companies") var companies: List<Company> = listOf(),
                        @SerializedName("poster_path") var posterPath: String,
                        @SerializedName("backdrop_path") var backdropPath: String,
                        @SerializedName("release_date") var releaseDate: String,
                        @SerializedName("vote_average") var rating: String,
                        @SerializedName("original_language")var language: String,
) {
    /**
     * Contains the release date separated as Year, Month, Day in List positions 0, 1, 2 respectively
     */
    val date: List<String>
        get() = releaseDate.split("-")
}