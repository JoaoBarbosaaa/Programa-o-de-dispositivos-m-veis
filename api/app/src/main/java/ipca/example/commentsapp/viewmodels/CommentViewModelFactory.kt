package ipca.example.commentsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ipca.example.commentsapp.CommentRepository
import ipca.example.commentsapp.viewmodels.CommentDetailViewModel
import ipca.example.commentsapp.viewmodels.CommentsListViewModel
import ipca.example.commentsapp.viewmodels.LikedCommentsViewModel

class CommentViewModelFactory(
    private val repository: CommentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CommentsListViewModel::class.java) -> {
                CommentsListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CommentDetailViewModel::class.java) -> {
                CommentDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LikedCommentsViewModel::class.java) -> {
                LikedCommentsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}