package ipca.example.commentsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipca.example.commentsapp.models.Comment
import ipca.example.commentsapp.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow // NOVO IMPORT
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
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

}