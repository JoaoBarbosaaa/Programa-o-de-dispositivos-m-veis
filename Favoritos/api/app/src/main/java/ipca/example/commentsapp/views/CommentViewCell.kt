package ipca.example.commentsapp.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ipca.example.commentsapp.models.Comment

@Composable
fun CommentViewCell(comment: Comment, navController: NavController) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Post ID: ${comment.postId}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "❤️ ${comment.likes} Gosto${if (comment.likes != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}