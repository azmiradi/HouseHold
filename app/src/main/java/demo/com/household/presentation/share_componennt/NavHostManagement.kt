package demo.com.household.presentation.share_componennt

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import demo.com.household.presentation.NavigationDestination
import demo.com.household.presentation.screens.auth.login.LoginScreen
import demo.com.household.presentation.screens.auth.signup.SignupScreen
import demo.com.household.presentation.screens.auth.splash.SplashScreen
import demo.com.household.presentation.screens.main.admin.add_product.AddProduct
import demo.com.household.presentation.screens.main.admin.orders.OrdersScreen
import demo.com.household.presentation.screens.main.home.HomeScreen
import demo.com.household.presentation.screens.main.user.cart.CartScreenScreen
import demo.com.household.presentation.screens.main.user.category.CategoriesScreen
import demo.com.household.presentation.screens.main.user.my_orders.MyOrdersScreen
import demo.com.household.presentation.screens.main.user.payment.PaymentScreen
import demo.com.household.presentation.screens.main.user.product.ProductScreen
import demo.com.household.presentation.screens.main.user.products.ProductsScreen

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
        composable(NavigationDestination.Login.destination) {
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
            HomeScreen(onNavigate = { destination, data ->
                if (destination == NavigationDestination.Login)
                    navController.navigate(destination.destination) {
                        popUpTo(NavigationDestination.Home.destination) {
                            inclusive = true
                        }
                    }
                else {
                    if (data.isEmpty()) {
                        navController.navigate(destination.destination)
                    } else if (destination == NavigationDestination.Products){
                        navController.navigate(
                            destination.destination.replace(
                                "{subCategory}",
                                data
                            )
                        )
                    }
                    else if (destination == NavigationDestination.SubCategory){
                        navController.navigate(
                            destination.destination.replace(
                                "{categoryID}",
                                data
                            )
                        )
                    }
                }

            }, onBack = {
                navController.popBackStack()
            })
        }
        composable(NavigationDestination.AddProduct.destination) {
            AddProduct(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            })
        }

        composable(NavigationDestination.Products.destination) {
            val data = it.arguments?.getString("subCategory")

            ProductsScreen(onNavigate = { destination, productID ->
                navController.navigate(
                    destination.destination.replace(
                        "{product}",
                        productID
                    )
                )
            }, onBack = {
                navController.popBackStack()
            }, categoryOb = data.toString())
        }

        composable(NavigationDestination.Product.destination) {
            val data = it.arguments?.getString("product")

            ProductScreen(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            }, productID = data.toString())
        }

        composable(NavigationDestination.Cart.destination) {
            CartScreenScreen(onNavigate = { destination, totalAmount ->
                navController.navigate(
                    destination.destination
                        .replace("{totalAmount}", totalAmount)
                )
            }, onBack = {
                navController.popBackStack()
            })
        }

        composable(NavigationDestination.Purchase.destination) {
            val totalAmount = it.arguments?.getString("totalAmount")

            PaymentScreen(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            }, totalAmount = totalAmount.toString())
        }

        composable(NavigationDestination.MyOrders.destination) {
            MyOrdersScreen(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            })
        }

        composable(NavigationDestination.Orders.destination) {
            OrdersScreen(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            })
        }

        composable(NavigationDestination.SubCategory.destination) {
            val data = it.arguments?.getString("categoryID")
            CategoriesScreen(onNavigate = {
                navController.navigate(it.destination)
            }, onBack = {
                navController.popBackStack()
            }, categoryID = data.toString())
        }
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