package arunkbabu90.popmovies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class TopMovieViewModel extends AndroidViewModel
{
    private final LiveData<List<MovieTopRated>> topMovies;

    public TopMovieViewModel(@NonNull Application application) {
        super(application);
        topMovies = MoviesDatabase.getInstance(this.getApplication()).movieDao().loadTopRatedMovies();
    }

    public LiveData<List<MovieTopRated>> getTopMovies() {
        return topMovies;
    }
}
