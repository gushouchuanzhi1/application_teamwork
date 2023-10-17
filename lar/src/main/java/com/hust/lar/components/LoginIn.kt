package com.hust.lar.components

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hust.lar.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Preview
@Composable
fun LoginIn(navController: NavHostController = rememberNavController()) {
    var userName by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var btEnabled by rememberSaveable {
        mutableStateOf(false)
    }
    var isError by rememberSaveable {
        mutableStateOf(false)
    }

    if (userName.isNotEmpty() and password.isNotEmpty()) {
        btEnabled = true
    }
    val scope = rememberCoroutineScope { Dispatchers.IO }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(text = "用户名", color = Color.Gray) },
            placeholder = { Text(text = "请输入邮箱", color = Color.LightGray) },
            leadingIcon = {
                Image(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                Row {
                    Text(text = " @qq.com", modifier = Modifier.padding(end = 5.dp))
                    AnimatedVisibility(
                        visible = userName.isNotEmpty(),
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        Image(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    userName = ""
                                    btEnabled = false
                                }
                                .padding(end = 15.dp)
                                .size(20.dp)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(20.dp),
            colors = textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        TextField(
            value = password,
            onValueChange = {
                if(it.length <= 10) {
                    password = it
                }else {

                }
            },
            modifier = Modifier
                .padding(top = 20.dp),
            isError = isError,
            label = { Text(text = "密码", color = if(isError) Color.Red else Color.Gray)},
            placeholder = { Text(text = if(isError) "账号或密码错误输入错误" else "请输入密码", color = if (isError) Color.Red else Color.LightGray, fontSize = 13.sp) },
            leadingIcon = { Image(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
            )},
            trailingIcon = {
                AnimatedVisibility(
                    visible = password.isNotEmpty(),
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Image(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                password = ""
                                btEnabled = false
                            }
                            .padding(start = 15.dp, end = 15.dp)
                            .size(20.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(20.dp),
            colors = textFieldColors(
                containerColor = if(isSystemInDarkTheme()) Color.White else Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = if(isSystemInDarkTheme()) Color.Transparent else Color.Gray,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                errorCursorColor = Color.Black
            ),
            singleLine = true
        )

        Button(
            onClick = {
                if(!Objects.equals(userName, "yuruop") || !Objects.equals(password, "123456")) {
                    isError = true
                    btEnabled = false
                    password = ""
                    scope.launch {
                        delay(800L)
                        isError = false
                    }

                } else {
                    // TODO
                }
            },
            modifier = Modifier
                .padding(top = 20.dp)
                .width(150.dp),
            enabled = btEnabled,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(R.color.teal_200),
                disabledContainerColor = Color.Gray
            )
        ) {
            Text(text = "登录")
        }
    }
}



