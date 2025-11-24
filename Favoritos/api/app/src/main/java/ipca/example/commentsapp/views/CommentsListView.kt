package ipca.example.commentsapp.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ipca.example.commentsapp.viewmodels.CommentsListViewModel
import ipca.example.commentsapp.views.components.CommentViewCell

@Composable
fun CommentsListView(
    navController: NavController,
    viewModel: CommentsListViewModel
){

    val comments by viewModel.commentsListFlow.collectAsState()

    val syncState by viewModel.syncState

    Scaffold(
        topBar = {

        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            if (syncState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (syncState.error != null && comments.isEmpty()) {
                Text(
                    text = "Erro ao carregar dados: ${syncState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (comments.isEmpty() && syncState.error == null) {
                Text(
                    text = "Nenhum comentário encontrado.",
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (comments.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(comments) { comment ->
                        CommentViewCell(comment = comment, navController = navController)
                    }
                }
            }
        }
    }
}