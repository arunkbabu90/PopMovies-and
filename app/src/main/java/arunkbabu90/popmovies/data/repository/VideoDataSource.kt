package arunkbabu90.popmovies.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arunkbabu90.popmovies.data.api.TMDBEndPoint
import arunkbabu90.popmovies.data.model.VideoResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VideoDataSource(private val apiService: TMDBEndPoint,
                      private val disposable: CompositeDisposable) {

    private val TAG = VideoDataSource::class.java.simpleName

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _fetchedVideos = MutableLiveData<VideoResponse>()
    val fetchedVideos: LiveData<VideoResponse>
        get() = _fetchedVideos

    fun fetchVideos(movieId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        disposable.add(
            apiService.getVideos(movieId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { videoResponse ->
                        _networkState.postValue(NetworkState.LOADED)
                        _fetchedVideos.postValue(videoResponse)
                    },
                    { e ->
                        Log.e(TAG, "fetchVideos: $e")
                        _networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }
}