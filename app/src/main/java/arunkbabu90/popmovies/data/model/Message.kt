package arunkbabu90.popmovies.data.model

/**
 * Data Object representing a single Message in the Chat Room
 */
data class Message(var key: String = "",
                   val msg: String = "",
                   val senderId: String = "",
                   val senderName: String = "",
                   val msgTimestamp: Long = -1) {
    companion object {
        const val TYPE_YOU = 4000
    }
}