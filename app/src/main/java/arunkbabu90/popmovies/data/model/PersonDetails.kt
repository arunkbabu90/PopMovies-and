package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class PersonDetails(val birthday: String? = "",
                         val deathDay:String? = "",
                         val biography: String = "",
                         @SerializedName("place_of_birth") val placeOfBirth: String? = "")
