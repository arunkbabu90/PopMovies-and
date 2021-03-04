package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

data class CastCrewResponse(@SerializedName("cast") val castList: List<Person>,
                            @SerializedName("crew") val crewList: List<Person>)