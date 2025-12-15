package ipca.example.commentsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipca.example.commentsapp.models.Comment
import ipca.example.commentsapp.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentDetailViewModel(private val repository: CommentRepository) : ViewModel() {

    private val _commentDetails = MutableStateFlow<Comment?>(null)
    val commentDetails: StateFlow<Comment?> = _commentDetails

    fun loadCommentDetails(commentId: String) {
        val idInt = commentId.toIntOrNull()
        if (idInt == null) {
            _commentDetails.value = null
            return
        }

        viewModelScope.launch {
            repository.getCommentById(idInt).collect { comment ->
                _commentDetails.value = comment
            }
        }
    }

    fun toggleLike() {
        val current = _commentDetails.value ?: return
        val liked = !current.isLikedByMe

        // Atualização otimista para UI imediata
        _commentDetails.value = current.copy(
            likes = if (liked) current.likes + 1 else current.likes - 1,
            isLikedByMe = liked
        )

        // Chama diretamente – o Repository executa em background
        repository.toggleLike(current.id, liked)
    }
}