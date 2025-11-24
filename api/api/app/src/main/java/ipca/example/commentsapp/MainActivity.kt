package ipca.example.commentsapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ipca.example.commentsapp.ui.theme.ApiTheme
import ipca.example.commentsapp.views.CommentDetailView
import ipca.example.commentsapp.views.CommentsListView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            CommentsListView(navController = navController)
        }

        composable(
            route = "detail/{commentId}",
            arguments = listOf(
                navArgument("commentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val commentId = backStackEntry.arguments?.getString("commentId") ?: ""

            CommentDetailView(
                navController = navController,
                commentId = commentId
            )
        }
    }
}