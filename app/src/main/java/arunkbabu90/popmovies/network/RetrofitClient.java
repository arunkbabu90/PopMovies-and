package arunkbabu90.popmovies.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Code adapted from https://www.androidhive.info/2016/05/android-working-with-retrofit-http-library/
 */
public class RetrofitClient
{
    // TMDB Example URL "http://api.themoviedb.org/3/movie/popular?api_key=<your_api_key>";
    private static final String TMDB_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
