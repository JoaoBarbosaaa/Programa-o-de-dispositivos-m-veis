package ipca.example.commentsapp

import ipca.example.commentsapp.models.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentRepository(private val commentDao: CommentDao) {

    fun getAllComments(): Flow<List<Comment>> {
        return commentDao.getAllCommentsForListScreen()
    }

    suspend fun insertAll(comments: List<Comment>) {
        comments.forEach { comment ->
            commentDao.insertComment(comment)
        }
    }

    fun getCommentById(commentId: Int): Flow<Comment?> {
        return commentDao.getCommentById(commentId)
    }

    fun getLikedComments(): Flow<List<Comment>> {
        return commentDao.getLikedComments()
    }


    fun toggleLike(commentId: Int, like: Boolean) {
        val increment = if (like) 1 else -1
        CoroutineScope(Dispatchers.IO).launch {
            commentDao.toggleLike(commentId, increment, like)  // <-- Nome correto
        }
    }
}