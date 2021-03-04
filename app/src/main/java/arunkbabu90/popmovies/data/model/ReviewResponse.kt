package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

class ReviewResponse(val page: Int,
                     @SerializedName("results") val reviews: List<Review>,
                     @SerializedName("total_pages") val totalPages: Int)