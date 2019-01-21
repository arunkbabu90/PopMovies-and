package arunkbabu90.popmovies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class FavouritesViewModel extends AndroidViewModel
{
    private final LiveData<List<Favourite>> favouriteMovies;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        favouriteMovies = MoviesDatabase.getInstance(this.getApplication()).movieDao().loadFavMovies();
    }

    public LiveData<List<Favourite>> getFavouriteMovies() {
        return favouriteMovies;
    }
}
