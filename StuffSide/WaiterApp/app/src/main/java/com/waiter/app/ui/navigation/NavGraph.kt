package com.waiter.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.waiter.app.ui.orders.*

object Routes {
    const val LIST = "orders"
    const val DETAILS = "order/{id}"
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val vm: OrdersViewModel = viewModel()

    NavHost(
        navController = nav,
        startDestination = Routes.LIST,
        modifier = modifier
    ) {
        composable(Routes.LIST) {
            OrdersListScreen(
                vm = vm,
                onOpenDetails = { id -> nav.navigate("order/$id") }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("id")?.let { id ->
                vm.select(id)
            }
            OrderDetailsScreen(
                vm = vm,
                onBack = { nav.popBackStack() }
            )
        }
    }
}
