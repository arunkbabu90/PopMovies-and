package arunkbabu90.popmovies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MovieViewModel extends AndroidViewModel
{
    private final LiveData<List<MoviePopular>> movies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        movies = MoviesDatabase.getInstance(this.getApplication()).movieDao().loadPopularMovies();
    }

    public LiveData<List<MoviePopular>> getMovies() {
        return movies;
    }
}