package arunkbabu90.popmovies.ui.viewmodel

import androidx.lifecycle.ViewModel
import arunkbabu90.popmovies.data.model.FavouritesLiveData
import arunkbabu90.popmovies.data.repository.MovieFavouriteRepository

class FavouritesViewModel : ViewModel() {
    private val repository: FavouritesRepository = MovieFavouriteRepository()

    fun getFavouritesLiveData() = repository.getFavouriteMovies()

    interface FavouritesRepository {
        fun getFavouriteMovies(): FavouritesLiveData?
    }
}