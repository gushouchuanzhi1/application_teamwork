package com.hust.lar.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Main(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_page") {
        composable("login_page") { LoginIn(navController) }
        composable("sign_page") { SignUp(navController) }
    }
}