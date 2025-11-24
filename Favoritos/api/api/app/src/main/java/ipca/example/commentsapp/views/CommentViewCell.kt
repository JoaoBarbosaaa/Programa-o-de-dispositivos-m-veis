package ipca.example.commentsapp.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ipca.example.commentsapp.models.Comment
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CommentViewCell(comment: Comment, navController: NavController) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                navController.navigate("detail/${comment.id}")
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post ID: ${comment.postId}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "❤️ ${comment.likes ?: 0} Gostos",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.body ?: "Corpo indisponível",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 2,
                //overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Por: ${comment.user?.username ?: "Anónimo"}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}