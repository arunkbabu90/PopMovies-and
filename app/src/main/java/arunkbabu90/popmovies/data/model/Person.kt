package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class Person(val id: Int = -1,
                  val name: String = "",
                  val gender: Int? = -1,
                  val popularity: Int = -1,
                  val order: Int = -1,
                  @SerializedName("original_name") val originalName: String = "",
                  @SerializedName("character") val characterName: String = "",
                  @SerializedName("profile_path") val dpPath: String? = "",
                  @SerializedName("known_for_department") val department: String = "")
