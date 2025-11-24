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

data class CommentDetailState(
    val comment: Comment? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CommentDetailViewModel : ViewModel() {

    var uiState = mutableStateOf(CommentDetailState())
        private set

    fun fetchCommentDetail(id: String) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val url = "https://dummyjson.com/comments/$id"

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

                    val commentResult = response.body!!.string()

                    try {
                        val jsonObject = JSONObject(commentResult)
                        val commentObject = Comment.fromJson(jsonObject)

                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            comment = commentObject,
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
