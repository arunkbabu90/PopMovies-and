package arunkbabu90.popmovies.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import arunkbabu90.popmovies.data.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { movieDb -> INSTANCE = movieDb }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                MovieDatabase::class.java, "Movies.db")
                .build()
    }
}