package ipca.example.commentsapp

import androidx.room.*
import ipca.example.commentsapp.models.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY id DESC")
    fun getCommentsForPost(postId: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments ORDER BY id DESC")
    fun getAllCommentsForListScreen(): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE id = :commentId")
    fun getCommentById(commentId: Int): Flow<Comment?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Update
    suspend fun updateComment(comment: Comment)

    @Delete
    suspend fun deleteComment(comment: Comment)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: Int)

    @Query("""
        UPDATE comments 
        SET likes = likes + :increment, 
            isLikedByMe = :liked 
        WHERE id = :commentId
    """)
    suspend fun toggleLike(commentId: Int, increment: Int, liked: Boolean)

    @Query("SELECT * FROM comments WHERE isLikedByMe = 1 ORDER BY id DESC")
    fun getLikedComments(): Flow<List<Comment>>
}