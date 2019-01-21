package arunkbabu90.popmovies.network;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import arunkbabu90.popmovies.database.MoviePopular;

public class MovieResponse
{
    @SerializedName("results") private List<MoviePopular> results;

    public List<MoviePopular> getPopularResults() {
        return results;
    }
}
