package ipca.example.commentsapp.repository

import ipca.example.commentsapp.db.CommentDao
import ipca.example.commentsapp.models.Comment
import kotlinx.coroutines.flow.Flow


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

}