package arunkbabu90.filimibeat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arunkbabu90.popmovies.data.model.VideoResponse
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.data.repository.VideoRepository
import io.reactivex.disposables.CompositeDisposable

class VideoViewModel(private val repository: VideoRepository, movieId: Int): ViewModel() {
    private val disposable = CompositeDisposable()

    val videoList: LiveData<VideoResponse> by lazy {
        repository.fetchVideos(disposable, movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        repository.getNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}