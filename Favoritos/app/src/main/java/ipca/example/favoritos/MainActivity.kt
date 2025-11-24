package ipca.example.favoritos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ipca.example.favoritos.presentation.ListaTarefasView
import ipca.example.favoritos.presentation.LoginView
import ipca.example.favoritos.presentation.PerfilView
//import ipca.example.favoritos.presentation.RegistarView
import ipca.example.favoritos.presentation.theme.FavoritosTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FavoritosTheme {
                AppNavegacao()
            }
        }
    }
}

@Composable
fun AppNavegacao() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginView(navController = navController)
        }

        composable("registar") {
            RegistarView(navController = navController)
        }

        composable("home") {
            HomeView(navController = navController)
        }

        composable("perfil") {
            PerfilView(navController = navController)
        }

        composable("tarefas") {
            ListaTarefasView(navController = navController)
        }

    }
}

@Composable
fun HomeView(navController: androidx.navigation.NavController) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏋️‍♂️ Bem-vindo aos teus lembretes",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))



            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate("perfil")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("tarefas")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Tarefas")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authSair()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Sair da Conta")
            }



        }
    }
}

private fun authSair() {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    auth.signOut()
}