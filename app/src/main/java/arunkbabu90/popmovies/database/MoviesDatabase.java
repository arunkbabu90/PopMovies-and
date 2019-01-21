package arunkbabu90.popmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MoviePopular.class, MovieTopRated.class, Favourite.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase
{
    private static MoviesDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviesdb";

    public static MoviesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MoviesDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}