package arunkbabu90.popmovies.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var Id: Int,
                 @SerializedName("id") @ColumnInfo(name = "movieId") var movieId: Int,
                 @SerializedName("poster_path") @ColumnInfo(name = "posterPath") var posterPath: String,
                 @SerializedName("backdrop_path") @ColumnInfo(name = "backdropPath") var backdropPath: String,
                 @SerializedName("title") @ColumnInfo(name = "title") var title: String,
                 @SerializedName("vote_average") @ColumnInfo(name = "rating") var rating: String,
                 @SerializedName("overview") @ColumnInfo(name = "overview") var overview: String,
                 @SerializedName("release_date") @ColumnInfo(name = "releaseDate") var releaseDate: String
) {

    /**
     * Contains the release date separated as Year, Month, Day in List positions 0, 1, 2 respectively
     */
    val date: List<String>
        get() = releaseDate.split("-")
}