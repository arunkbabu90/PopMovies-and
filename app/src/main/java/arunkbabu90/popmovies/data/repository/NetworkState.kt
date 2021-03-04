package arunkbabu90.popmovies.data.repository

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
}

class NetworkState private constructor(val status: Status, val msg: String) {
    companion object {
        val LOADED: NetworkState = NetworkState(Status.SUCCESS, "Success")
        val LOADING: NetworkState = NetworkState(Status.RUNNING, "Running")
        val ERROR: NetworkState = NetworkState(Status.FAILED, "Network Error")
        val EOL: NetworkState = NetworkState(Status.FAILED, "End of list")
        val CLEAR: NetworkState = NetworkState(Status.RUNNING, "")
    }
}