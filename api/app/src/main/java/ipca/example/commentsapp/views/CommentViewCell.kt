package ipca.example.commentsapp.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ipca.example.commentsapp.models.Comment
import ipca.example.commentsapp.viewmodels.CommentsListViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.platform.LocalContext

@Composable
fun CommentViewCell(comment: Comment, navController: NavController, viewModel: CommentsListViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable {
                val commentIdString = comment.id.toString()
                if (commentIdString.isNotEmpty() && comment.id > 0) {
                    navController.navigate("detail/${commentIdString}")
                } else {
                    println("ERRO DE NAVEGAÇÃO: ID de comentário inválido ou ausente: ${comment.id}")
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = comment.body,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post ID: ${comment.postId}",
                    style = MaterialTheme.typography.labelSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconToggleButton(
                        checked = comment.isLikedByMe,
                        onCheckedChange = { liked ->
                            viewModel.toggleLike(comment.id, liked)
                        }
                    ) {
                        Icon(
                            imageVector = if (comment.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (comment.isLikedByMe) Color.Red else Color.Gray
                        )
                    }
                    Text(
                        text = "${comment.likes} Gosto${if (comment.likes != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}