package com.waiter.app.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.waiter.app.ui.auth.AuthViewModel
import com.waiter.app.ui.auth.LoginScreen
import com.waiter.app.ui.auth.RegisterScreen
import com.waiter.app.ui.orders.OrderDetailsScreen
import com.waiter.app.ui.orders.OrdersListScreen
import com.waiter.app.ui.orders.OrdersViewModel
import com.waiter.app.ui.settings.SettingsScreen
import com.waiter.app.ui.settings.SettingsViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraph.Companion.findStartDestination

// Оновлюємо об'єкт Routes
object Routes {
    // Графи
    const val ROOT_GRAPH = "root_graph"
    const val AUTH_GRAPH = "auth_graph"
    const val MAIN_GRAPH = "main_graph"

    // Екрани
    const val SPLASH_SCREEN = "splash" // Тимчасовий екран для перевірки логіну
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val LIST = "orders_list"
    const val DETAILS = "order/{id}"
    const val SETTINGS = "settings" // Додамо екран налаштувань у граф
}

/**
 * Головний NavGraph, який замінить AppNav в MainActivity.
 * Він вирішує, показати AuthGraph чи MainGraph.
 */
@Composable
fun RootNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Нам потрібен SettingsViewModel на рівні всього графу,
    // щоб перевіряти стан логіну та виконувати logout.
    val context = LocalContext.current
    val viewModelStoreOwner = checkNotNull(context as? ViewModelStoreOwner) {
        "Current context is not a ViewModelStoreOwner. Make sure this NavHost is in a ComponentActivity."
    }
    val settingsViewModel: SettingsViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner
    )
    val isLoggedIn by settingsViewModel.isLoggedInFlow.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH_SCREEN,
        route = Routes.ROOT_GRAPH,
        modifier = modifier
    ) {

        // 1. Екран-перевірка
        composable(Routes.SPLASH_SCREEN) {
            // Поки isLoggedIn не завантажився (null), нічого не робимо
            LaunchedEffect(isLoggedIn) {
                when (isLoggedIn) {
                    true -> { // Юзер залогінений
                        navController.navigate(Routes.MAIN_GRAPH) {
                            popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
                        }
                    }
                    false -> { // Юзер не залогінений
                        navController.navigate(Routes.AUTH_GRAPH) {
                            popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
                        }
                    }
                    null -> {
                        // DataStore ще завантажується, чекаємо
                    }
                }
            }
        }

        // 2. Граф Автентифікації
        navigation(
            startDestination = Routes.LOGIN,
            route = Routes.AUTH_GRAPH
        ) {
            composable(Routes.LOGIN) {
                val authViewModel: AuthViewModel = viewModel()
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        // Успіх -> переходимо на головний граф
                        navController.navigate(Routes.MAIN_GRAPH) {
                            popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                        }
                    },
                    // !! ВАЖЛИВО: передаємо функцію збереження сесії
                    onLoginSuccessSaveSession = { id, name ->
                        settingsViewModel.saveLoginSession(id, name)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }
            composable(Routes.REGISTER) {
                val authViewModel: AuthViewModel = viewModel()
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.popBackStack() // Повертаємось на логін
                    },
                    onBack = {
                        navController.popBackStack() // Повертаємось на логін
                    }
                )
            }
        }

        // 3. Граф Основного Додатку
        navigation(
            startDestination = Routes.LIST,
            route = Routes.MAIN_GRAPH
        ) {
            // Загальний ViewModel для OrdersList та OrderDetails
            composable(Routes.LIST) {
                // Отримуємо backStackEntry для 'MAIN_GRAPH'
                val mainGraphEntry = remember(it) {
                    navController.getBackStackEntry(Routes.MAIN_GRAPH)
                }
                // Створюємо VM, при'язаний до цього графу, щоб він був спільним
                val ordersViewModel: OrdersViewModel = viewModel(viewModelStoreOwner = mainGraphEntry)

                OrdersListScreen(
                    vm = ordersViewModel,
                    onOpenDetails = { id -> navController.navigate("order/$id") },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }

            composable(
                route = Routes.DETAILS,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                // Отримуємо той самий спільний ViewModel
                val mainGraphEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.MAIN_GRAPH)
                }
                val ordersViewModel: OrdersViewModel = viewModel(viewModelStoreOwner = mainGraphEntry)

                backStackEntry.arguments?.getString("id")?.let { id ->
                    ordersViewModel.select(id)
                }
                OrderDetailsScreen(
                    vm = ordersViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SETTINGS) {
                // settingsViewModel вже створений на рівні RootNavGraph,
                // тому ми просто використовуємо його
                SettingsScreen(
                    vm = settingsViewModel,
                    onLogout = {
                        // Виходимо -> переходимо на граф автентифікації
                        settingsViewModel.logout()
                        navController.navigate(Routes.AUTH_GRAPH) {
                            // Повністю очищуємо back stack, щоб не можна було повернутись "назад"
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

// Старий AppNav можна видалити або закоментувати
// @Composable
// fun AppNav(modifier: Modifier = Modifier) { ... }