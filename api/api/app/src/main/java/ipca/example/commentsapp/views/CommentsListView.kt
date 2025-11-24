package ipca.example.commentsapp.views
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ipca.example.commentsapp.viewmodels.CommentsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsListView(navController: NavController,
                     viewModel: CommentsListViewModel = viewModel()) {
    val state = viewModel.uiState.value

    LaunchedEffect(Unit) {
        viewModel.fetchComments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comentários (${state.comments.size})",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->


        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Erro ao carregar dados: ${state.error}", color = MaterialTheme.colorScheme.error)
            }
        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(state.comments) { comment ->
                    CommentViewCell(comment = comment, navController = navController) // 🌟 Passa o NavController
                }
            }
        }
    }
}