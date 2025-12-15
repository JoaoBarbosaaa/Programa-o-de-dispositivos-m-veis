package ipca.example.commentsapp.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipca.example.commentsapp.models.Comment
import ipca.example.commentsapp.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

class CommentsListViewModel(private val repository: CommentRepository) : ViewModel() {

    val commentsListFlow: StateFlow<List<Comment>> = repository.getAllComments()
        .map { list ->
            list.map { it.copy() } // Força emissão sempre
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    var syncState = mutableStateOf(CommentsListState(isLoading = false, error = null))
        private set

    init {
        fetchComments()
    }

    fun fetchComments() {
        syncState.value = syncState.value.copy(isLoading = true, error = null)

        val url = "https://dummyjson.com/comments"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                syncState.value = syncState.value.copy(
                    isLoading = false,
                    error = "Erro de rede: ${e.message}"
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        syncState.value = syncState.value.copy(
                            isLoading = false,
                            error = "Erro no servidor: Código ${response.code}"
                        )
                        return
                    }

                    val commentsResult = response.body!!.string()

                    try {
                        val jsonResult = JSONObject(commentsResult)
                        val commentsList = Comment.fromJsonArray(jsonResult)

                        viewModelScope.launch(Dispatchers.IO) {
                            repository.insertAll(commentsList)
                        }

                        syncState.value = syncState.value.copy(
                            isLoading = false,
                            error = null
                        )

                    } catch (e: Exception) {
                        syncState.value = syncState.value.copy(
                            isLoading = false,
                            error = "Erro ao analisar dados: ${e.message}"
                        )
                    }
                }
            }
        })
    }


    fun toggleLike(commentId: Int, liked: Boolean) {
        repository.toggleLike(commentId, liked)
    }
}