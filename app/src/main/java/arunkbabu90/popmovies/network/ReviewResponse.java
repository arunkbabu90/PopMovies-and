package arunkbabu90.popmovies.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import arunkbabu90.popmovies.Reviews;

public class ReviewResponse
{
    @SerializedName("results") private List<Reviews> reviewResults;

    public List<Reviews> getReviewResults() {
        return reviewResults;
    }
}
