package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class Video(val id: String = "",
                 val type: String = "",
                 val site: String = "",
                 @SerializedName("key") val videoId: String = "",
                 @SerializedName("name") val title: String = "")
