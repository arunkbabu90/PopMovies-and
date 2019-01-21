package arunkbabu90.popmovies.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import arunkbabu90.popmovies.database.MovieTopRated;

public class TopMovieResponse
{
    @SerializedName("results") private List<MovieTopRated> results;

    public List<MovieTopRated> getResults() {
        return results;
    }
}
