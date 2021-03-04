package arunkbabu90.popmovies

class Constants {
    companion object {
        @JvmStatic
        var userType: Int = -1
        var userId = ""
        var userFullName = ""
        var isAccountActivated = true

        const val COLLECTION_USERS = "Users"
        const val COLLECTION_FAVOURITES = "Favourites"

        const val FIELD_USER_TYPE = "userType"
        const val FIELD_FULL_NAME = "name"
        const val FIELD_ACCOUNT_VERIFIED = "activated"
        const val FIELD_DP_PATH = "dpPath"
        const val FIELD_TITLE = "title"
        const val FIELD_POSTER_PATH = "posterPath"
        const val FIELD_RELEASE_DATE = "releaseDate"
        const val FIELD_RATING = "rating"
        const val FIELD_OVERVIEW = "overview"
        const val FIELD_BACKDROP_PATH = "backdropPath"

        const val FIELD_TIMESTAMP = "timestamp"

        const val USER_TYPE_GUEST = 0
        const val USER_TYPE_PERSON = 1

        const val DIRECTORY_PROFILE_PICTURE = "/ProfilePictures/"
        const val PROFILE_PICTURE_FILE_NAME = "IMG_USER_PROFILE_PICTURE"
        const val IMG_FORMAT_JPG = ".jpg"

        const val DP_UPLOAD_SIZE = 960

        const val ROOT_MOVIE_ROOMS = "MovieRooms"
        const val FIELD_MESSAGE = "msg"
        const val FIELD_SENDER_ID = "senderId"
        const val FIELD_SENDER_NAME = "senderName"
        const val FIELD_MSG_TIMESTAMP = "msgTimestamp"
    }
}