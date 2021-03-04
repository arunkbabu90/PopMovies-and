package arunkbabu90.popmovies.data.model

import com.google.gson.annotations.SerializedName

class Review(val id: String = "",
             val author: String = "",
             val content: String = "",
             @SerializedName("updated_at") val updatedAt: String = "") {

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Review

        if (id != other.id) return false
        if (author != other.author) return false
        if (content != other.content) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }
}