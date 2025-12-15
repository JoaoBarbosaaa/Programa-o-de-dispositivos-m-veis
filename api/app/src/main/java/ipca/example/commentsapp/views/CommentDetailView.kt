package ipca.example.commentsapp.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ipca.example.commentsapp.viewmodels.CommentDetailViewModel
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDetailView(
    navController: NavController,
    commentId: String,
    viewModel: CommentDetailViewModel
) {

    LaunchedEffect(commentId) {
        viewModel.loadCommentDetails(commentId)
    }

    val comment by viewModel.commentDetails.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Comentário") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            if (comment == null) {
                val idInt = commentId.toIntOrNull()
                if (idInt != null) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "ID de Comentário Inválido.", color = MaterialTheme.colorScheme.error)
                }
            } else {

                val loadedComment = comment!!

                Text(
                    text = loadedComment.body,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Novo botão de like
                IconToggleButton(
                    checked = loadedComment.isLikedByMe,
                    onCheckedChange = { viewModel.toggleLike() }
                ) {
                    Icon(
                        imageVector = if (loadedComment.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (loadedComment.isLikedByMe) Color.Red else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Outros dados
                Text(
                    text = "Post ID: ${loadedComment.postId} | Likes: ${loadedComment.likes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}