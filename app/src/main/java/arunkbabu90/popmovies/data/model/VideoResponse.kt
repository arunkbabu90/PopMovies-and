package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class VideoResponse(@SerializedName("results") val videos: List<Video>)
