package fr.sercurio.soulseek

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fr.sercurio.soulseek.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Main

@Composable
fun NavigationGraph(
    soulseekApi: SoulseekApi
) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController, startDestination = Login) {
        composable<Login> { backStackEntry ->
            val login: Login = backStackEntry.toRoute()
            LoginScreen(
                soulseekApi,
                onNavigateToMain = {
                    navController.navigate(
                        route = Main
                    )
                })
        }
        composable<Main> { backStackEntry ->
            val main: Main = backStackEntry.toRoute()
            MainScreen(soulseekApi)
        }
    }
}