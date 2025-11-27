package com.waiter.app.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.waiter.app.core.UserRole
import com.waiter.app.ui.auth.*
import com.waiter.app.ui.delivery.DeliveriesScreen
import com.waiter.app.ui.orders.*
import com.waiter.app.ui.settings.SettingsScreen
import com.waiter.app.ui.settings.SettingsViewModel

object Routes {
    const val ROLE_SELECTION = "role_selection"

    // Нові графи
    const val WAITER_GRAPH = "waiter_graph"
    const val COURIER_GRAPH = "courier_graph"

    const val SPLASH_SCREEN = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val AUTH_GRAPH = "auth_graph"

    const val LIST = "orders_list"
    const val DETAILS = "order/{id}"
    const val SETTINGS = "settings"
}

@Composable
fun RootNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val viewModelStoreOwner = checkNotNull(context as? ViewModelStoreOwner) {
        "Current context is not a ViewModelStoreOwner."
    }
    val settingsViewModel: SettingsViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner)

    val isLoggedIn by settingsViewModel.isLoggedInFlow.collectAsState(initial = null)
    val userRoleString by settingsViewModel.userRoleFlow.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH_SCREEN,
        route = "root_graph",
        modifier = modifier
    ) {

        // 1. Splash / Routing Logic
        composable(Routes.SPLASH_SCREEN) {
            LaunchedEffect(isLoggedIn, userRoleString) {
                if (isLoggedIn == true && userRoleString != null) {
                    if (userRoleString == "WAITER") {
                        navController.navigate(Routes.WAITER_GRAPH) { popUpTo(Routes.SPLASH_SCREEN) { inclusive = true } }
                    } else {
                        navController.navigate(Routes.COURIER_GRAPH) { popUpTo(Routes.SPLASH_SCREEN) { inclusive = true } }
                    }
                } else if (isLoggedIn == false) {
                    navController.navigate(Routes.ROLE_SELECTION) { popUpTo(Routes.SPLASH_SCREEN) { inclusive = true } }
                }
            }
        }

        // 2. Вибір ролі
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    // Переходимо на логін з параметром ролі
                    navController.navigate("${Routes.LOGIN}/${role.name}")
                }
            )
        }

        // 3. Auth Graph (з параметром role)
        navigation(
            startDestination = "${Routes.LOGIN}/{role}",
            route = Routes.AUTH_GRAPH
        ) {
            composable(
                route = "${Routes.LOGIN}/{role}",
                arguments = listOf(navArgument("role") { type = NavType.StringType })
            ) { entry ->
                val roleName = entry.arguments?.getString("role") ?: "WAITER"
                val role = UserRole.valueOf(roleName)

                val authViewModel: AuthViewModel = viewModel()

                LoginScreen(
                    authViewModel = authViewModel,
                    // Передаємо роль в UI, якщо потрібно відобразити "Вхід для кур'єра"
                    // Але тут головне передати її у VM при натисканні кнопки:
                    role = role,

                    onLoginSuccessSaveSession = { id, name ->
                        settingsViewModel.saveLoginSession(id, name, role.name)
                    },
                    onLoginSuccess = {
                        if (role == UserRole.WAITER) {
                            navController.navigate(Routes.WAITER_GRAPH) { popUpTo(Routes.ROLE_SELECTION) { inclusive = true } }
                        } else {
                            navController.navigate(Routes.COURIER_GRAPH) { popUpTo(Routes.ROLE_SELECTION) { inclusive = true } }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("${Routes.REGISTER}/${role.name}")
                    }
                )
            }

            composable(
                route = "${Routes.REGISTER}/{role}",
                arguments = listOf(navArgument("role") { type = NavType.StringType })
            ) { entry ->
                val roleName = entry.arguments?.getString("role") ?: "WAITER"
                val role = UserRole.valueOf(roleName)
                val authViewModel: AuthViewModel = viewModel()

                RegisterScreen(
                    authViewModel = authViewModel,
                    role = role, // Передаємо роль
                    onRegisterSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // 4. Граф Офіціанта (Старий MAIN_GRAPH)
        navigation(route = Routes.WAITER_GRAPH, startDestination = Routes.LIST) {
            composable(Routes.LIST) {
                val graphEntry = remember(it) { navController.getBackStackEntry(Routes.WAITER_GRAPH) }
                val vm: OrdersViewModel = viewModel(viewModelStoreOwner = graphEntry)

                OrdersListScreen(
                    vm = vm,
                    onOpenDetails = { id -> navController.navigate("order/$id") },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }

            composable(
                route = Routes.DETAILS,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val graphEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.WAITER_GRAPH) }
                val vm: OrdersViewModel = viewModel(viewModelStoreOwner = graphEntry)
                backStackEntry.arguments?.getString("id")?.let { vm.select(it) }

                OrderDetailsScreen(vm = vm, onBack = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    vm = settingsViewModel,
                    onLogout = {
                        settingsViewModel.logout()
                        navController.navigate(Routes.ROLE_SELECTION) {
                            popUpTo(0) { inclusive = true } // Очистити все
                        }
                    }
                )
            }
        }

        // 5. Граф Кур'єра (Новий)
        navigation(route = Routes.COURIER_GRAPH, startDestination = "courier_home") {
            composable("courier_home") {
                val userId by settingsViewModel.userIdFlow.collectAsState(0)

                DeliveriesScreen(
                    courierId = userId,
                    onLogout = {
                        settingsViewModel.logout()
                        navController.navigate(Routes.ROLE_SELECTION) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}