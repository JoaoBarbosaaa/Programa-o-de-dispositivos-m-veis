package ipca.example.commentsapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ipca.example.commentsapp.viewmodels.LikedCommentsViewModel
import ipca.example.commentsapp.viewmodels.CommentsListViewModel
import ipca.example.commentsapp.views.components.CommentViewCell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedCommentsView(
    navController: NavController,
    viewModel: LikedCommentsViewModel,
    listViewModel: CommentsListViewModel
) {
    val likedComments by viewModel.likedCommentsFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentários com Like") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (likedComments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum comentário com like ainda.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = likedComments,
                        key = { it.id }
                    ) { comment ->
                        CommentViewCell(
                            comment = comment,
                            navController = navController,
                            viewModel = listViewModel
                        )
                    }
                }
            }
        }
    }
}