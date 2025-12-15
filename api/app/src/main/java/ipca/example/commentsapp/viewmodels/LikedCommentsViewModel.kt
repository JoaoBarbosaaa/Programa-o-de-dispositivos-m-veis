package ipca.example.commentsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipca.example.commentsapp.models.Comment
import ipca.example.commentsapp.CommentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LikedCommentsViewModel(private val repository: CommentRepository) : ViewModel() {

    val likedCommentsFlow: StateFlow<List<Comment>> = repository.getLikedComments()
        .map { list ->
            list.map { it.copy() } // Força emissão sempre
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
}