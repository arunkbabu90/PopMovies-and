package arunkbabu90.popmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favourites")
public class Favourite
{
    @PrimaryKey @ColumnInfo(name = "FavID") private int movieId;
    @ColumnInfo(name = "Title") private String movieTitle;
    @ColumnInfo(name = "Poster Path") private String posterPath;
    @ColumnInfo(name = "Cover Path") private String backDropPath;
    @ColumnInfo(name = "Synopsis") private String overview;
    @ColumnInfo(name = "Release Date") private String releaseDate;
    @ColumnInfo(name = "Rating") private double rating;
    @ColumnInfo(name = "Timestamp") private long timestamp;


    public Favourite(int movieId, String movieTitle, String posterPath, String backDropPath, double rating,
                     String overview, String releaseDate, long timestamp) {

        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterPath = posterPath;
        this.backDropPath = backDropPath;
        this.rating = rating;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.timestamp = timestamp;
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
        return Double.toString(getRating()) + "/10";
    }


    /**
     * Get the rating of the movie prefixed with "Rating: "
     * @return Prefixed Rating
     */
    public String getPrefixedRating() {
        return "Rating:       " + Double.toString(getRating());
    }


    public String getPosterPath() {
        return posterPath;
    }

    public String getBackDropPath() {
        return backDropPath;
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

    /**
     * Returns the the release year with the prefix "Release Date: "
     * @return Prefixed Release Year
     */
    public String getPrefixedReleaseYear() {
        return "Released:   " + getReleaseYear();
    }




    // List of Setter Methods

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
