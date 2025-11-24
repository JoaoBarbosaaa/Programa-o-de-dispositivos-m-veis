package ipca.example.commentsapp.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ipca.example.commentsapp.models.Comment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

data class CommentsListState(
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CommentsListViewModel : ViewModel() {

    var uiState = mutableStateOf(CommentsListState())
        private set

    fun fetchComments() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val url = "https://dummyjson.com/comments"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful){
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Erro no servidor: Código ${response.code}"
                        )
                        return
                    }

                    val commentsResult = response.body!!.string()

                    try {
                        val jsonResult = JSONObject(commentsResult)

                        val commentsList = Comment.fromJsonArray(jsonResult)

                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            comments = commentsList,
                            error = null
                        )
                    } catch (e: Exception) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = "Erro ao analisar dados: ${e.message}"
                        )
                    }
                }
            }
        })
    }
}
