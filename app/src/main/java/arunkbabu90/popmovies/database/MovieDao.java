package arunkbabu90.popmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao
{
    @Query("SELECT * FROM popular_movies")
    LiveData<List<MoviePopular>> loadPopularMovies();

    @Query("SELECT * FROM top_rated_movies")
    LiveData<List<MovieTopRated>> loadTopRatedMovies();

    @Query("SELECT * FROM favourites ORDER BY Timestamp DESC")
    LiveData<List<Favourite>> loadFavMovies();

    @Query("SELECT FavID FROM favourites WHERE FavID = :id")
    int loadFavouritesById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavMovie(Favourite movie);

    @Delete
    void deleteFavMovie(Favourite movie);

    @Query("DELETE FROM favourites WHERE FavID = :id")
    void deleteFavMovieById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPopularMovies(MoviePopular... movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTopRatedMovies(MovieTopRated... movies);
}