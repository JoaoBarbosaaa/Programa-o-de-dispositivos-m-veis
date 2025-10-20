package ipca.example.commentsapp.models
import org.json.JSONArray
import org.json.JSONObject

data class User (
    var id       : Int?    = null,
    var username : String? = null,
    var fullName : String? = null
) {
    companion object {
        fun fromJson(json : JSONObject) : User {
            return User(
                json.optInt("id"),
                json.optString("username"),
                json.optString("fullName")
            )
        }
    }
}

data class Comment (
    var id      : Int?    = null,
    var body    : String? = null,
    var postId  : Int?    = null,
    var likes   : Int?    = null,
    var user    : User?   = null
) {
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