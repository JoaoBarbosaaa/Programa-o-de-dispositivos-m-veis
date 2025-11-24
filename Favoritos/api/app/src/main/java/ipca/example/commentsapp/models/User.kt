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