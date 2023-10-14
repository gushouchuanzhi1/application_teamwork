package com.hust.mychat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hust.mychat.R
import com.hust.mychat.ui.theme.MyChatTheme

@Composable
fun Welcome() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.welcome),
            contentDescription = "splash"
        )
    }

}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    MyChatTheme {
        Welcome()
    }
}