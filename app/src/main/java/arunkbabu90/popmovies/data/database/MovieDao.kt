package arunkbabu90.popmovies.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arunkbabu90.popmovies.data.model.Movie
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MovieDao {
    @Query("SELECT * FROM Movie")
    fun getAllMovies(): Flowable<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(vararg movies: Movie): Completable

    @Query("DELETE FROM Movie")
    fun clearAllMovies()
}