package ipca.example.commentsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ipca.example.commentsapp.db.AppDatabase
import ipca.example.commentsapp.repository.CommentRepository
import ipca.example.commentsapp.ui.theme.ApiTheme
import ipca.example.commentsapp.views.CommentDetailView
import ipca.example.commentsapp.views.CommentsListView
import ipca.example.commentsapp.viewmodels.CommentDetailViewModel
import ipca.example.commentsapp.viewmodels.CommentsListViewModel

class MainActivity : ComponentActivity() {

    private lateinit var repository: CommentRepository
    private lateinit var viewModelFactory: CommentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        repository = CommentRepository(database.commentDao())

        viewModelFactory = CommentViewModelFactory(repository)

        setContent {
            ApiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModelFactory)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModelFactory: CommentViewModelFactory) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            val commentsListViewModel: CommentsListViewModel = viewModel(factory = viewModelFactory)

            CommentsListView(
                navController = navController,
                viewModel = commentsListViewModel
            )
        }

        composable(
            route = "detail/{commentId}",
            arguments = listOf(
                navArgument("commentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val commentId = backStackEntry.arguments?.getString("commentId") ?: ""

            val commentDetailViewModel: CommentDetailViewModel = viewModel(factory = viewModelFactory)

            CommentDetailView(
                navController = navController,
                commentId = commentId,
                viewModel = commentDetailViewModel
            )
        }
    }
}