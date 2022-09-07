package demo.com.household.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import demo.com.household.presentation.screens.auth.login.LoginScreen
import demo.com.household.presentation.screens.auth.signup.SignupScreen
import demo.com.household.presentation.screens.auth.splash.SplashScreen
import demo.com.household.presentation.screens.main.user.home.HomeScreen

@Composable
fun NavHostManagement() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Splash.destination
    ) {
        composable(NavigationDestination.Splash.destination) {
            SplashScreen(onNavigate = {
                navController.navigate(it.destination) {
                    popUpTo(NavigationDestination.Splash.destination) {
                        inclusive = true
                    }
                }
            })
        }
        composable(NavigationDestination.Login.destination  ) {
            //val accountType = it.arguments?.getString("accountType")
            LoginScreen(onNavigate = {

                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            })
        }
        composable(NavigationDestination.Signup.destination) {
            SignupScreen(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            })
        }
        composable(NavigationDestination.Home.destination) {
            HomeScreen(onNavigate = {
                if (it == NavigationDestination.Login)
                    navController.navigate(it.destination) {
                        popUpTo(NavigationDestination.Home.destination) {
                            inclusive = true
                        }
                    }
                else
                    navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            })
        }
//        composable(NavigationDestination.Categories.destination) {
//            CategoriesScreen(onNavigate = {
//                navController.navigate(it.destination)
//            }, onBack = {
//                navController.popBackStack()
//            })
//        }
//        composable(NavigationDestination.AddSpend.destination) {
//            AddSpendScreen(onNavigate = {
//                navController.navigate(it.destination)
//            }, onBack = {
//                navController.popBackStack()
//            })
//        }
//
//        composable(NavigationDestination.MonthlyReport.destination) {
//            MonthlyReportScreen(onNavigate = {
//                navController.navigate(it.destination)
//            }, onBack = {
//                navController.popBackStack()
//            })
//        }
//
//        composable(NavigationDestination.AllSpend.destination) {
//            SpendDetailsScreen(onNavigate = {
//                navController.navigate(it.destination)
//            }, onBack = {
//                navController.popBackStack()
//            })
//        }
//
//        dialog(
//            NavigationDestination.WarningMoney.destination,
//            dialogProperties = DialogProperties(dismissOnBackPress = true)
//        ) {
//            WarningMoneyScreen {
//                navController.popBackStack()
//            }
//        }

    }
}