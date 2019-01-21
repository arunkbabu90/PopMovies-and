package arunkbabu90.popmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import arunkbabu90.popmovies.fragments.PopularFragment;

@Entity(tableName = "popular_movies")
public class MoviePopular
{
    @Ignore private final String IMG_SIZE_MID = "/w342";
    @Ignore private final String IMG_SIZE_LARGE = "/w780";

    @PrimaryKey @ColumnInfo(name = "MovieID") @SerializedName("id") private int movieId;
    @ColumnInfo(name = "Title") @SerializedName("title") private String movieTitle;
    @ColumnInfo(name = "Rating") @SerializedName("vote_average") private double rating;
    @ColumnInfo(name = "Release Date") @SerializedName("release_date") private String releaseDate;
    @ColumnInfo(name = "Overview") @SerializedName("overview") private String overview;
    @ColumnInfo(name = "Poster Path") @SerializedName("poster_path") private String posterPath;
    @ColumnInfo(name = "CoverPath") @SerializedName("backdrop_path") private String backDropPath;
    @ColumnInfo(name = "IsFavorite") private int isFavourite;


    public MoviePopular(int movieId, String posterPath, String backDropPath, String movieTitle, double rating,
                        String overview, String releaseDate) {

        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backDropPath = backDropPath;
    }

    /**
     * Retrieves the id of the selected movie
     * @return Integer id of the movie
     */
    public int getMovieId() {
        return movieId;
    }

    /**
     * Returns just the rating value in double
     * @return The rating value
     */
    public double getRating() {
        return rating;
    }

    /**
     * Retrieves the average rating of the selected movie out of 10
     * @return The Double precision floating point rating of the movie converted into String
     */
    public String getStringRating() {
        return Double.toString(rating) + "/10";
    }


    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Extracts the Poster URL of the selected movie
     * @return String Path of the poster
     */
    public String getPosterUrl() {
        return PopularFragment.BASE_IMG_URL + IMG_SIZE_MID + getPosterPath();
    }

    public String getBackDropPath() {
        return backDropPath;
    }

    /**
     * Extracts the Cover URL from the selected movie
     * @return String Path of the cover
     */
    public String getBackDropUrl() {
        return PopularFragment.BASE_IMG_URL + IMG_SIZE_LARGE + getBackDropPath();
    }

    /**
     * Retrieves the movieTitle of the selected movie
     * @return String movieTitle of the movie
     */
    public String getMovieTitle() {
        return movieTitle;
    }

    /**
     * Retrieves the description of the selected movie
     * @return String description of the movie
     */
    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Retrieves the release year of the selected movie
     * @return String release year of the movie
     */
    public String getReleaseYear() {
        // Extract only the release Year from the Release Year. Like: From: 2018-02-01 --> To: 2018
        int yrIndex = getReleaseDate().indexOf("-");
        if (yrIndex == -1) {
            return getReleaseDate();
        }
        return getReleaseDate().substring(0, yrIndex);
    }


    // List of setter methods

    public void setBackDropPath(String backDropPath) {
        this.backDropPath = backDropPath;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }




    public int isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }
}
