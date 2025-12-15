package ipca.example.commentsapp.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "comments")
data class Comment (
    @PrimaryKey
    var id      : Int,
    var body    : String,
    var postId  : Int,
    var likes   : Int,
    var isLikedByMe: Boolean = false
) {
    @Ignore
    var user    : User? = null

    constructor(
        id: Int?,
        body: String?,
        postId: Int?,
        likes: Int?,
        user: User?
    ) : this(
        id ?: 0,
        body ?: "",
        postId ?: 0,
        likes ?: 0
    ) {
        this.user = user
    }

    companion object {
        fun fromJson(json : JSONObject) : Comment {
            return Comment(
                json.optInt("id"),
                json.optString("body"),
                json.optInt("postId"),
                json.optInt("likes"),
                if (json.has("user")) User.fromJson(json.getJSONObject("user")) else null
            )
        }

        fun fromJsonArray(json : JSONObject) : List<Comment> {
            val commentsList = mutableListOf<Comment>()

            val jsonArray : JSONArray = json.optJSONArray("comments") ?: return commentsList

            for (i in 0 until jsonArray.length()) {
                val commentJson = jsonArray.getJSONObject(i)
                commentsList.add(fromJson(commentJson))
            }
            return commentsList
        }
    }
}