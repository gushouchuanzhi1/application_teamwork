package com.hust.lar.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hust.resbase.RouteConfig

@Composable
fun Main(jumpToHome: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RouteConfig.LOGIN_PAGE) {
        composable(RouteConfig.LOGIN_PAGE) { LoginIn(navController, jumpToHome) }
        composable(RouteConfig.SIGN_PAGE) { SignUp(navController) }
    }
}