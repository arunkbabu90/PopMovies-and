package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class Person(val name: String = "",
                  @SerializedName("character") val characterName: String = "",
                  @SerializedName("profile_path") val dpPath: String = "",
                  @SerializedName("known_for_department") val department: String = "")
