package com.hust.lar.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hust.lar.viewmodels.LARActivityViewModel
import com.hust.resbase.RouteConfig
import com.hust.resbase.TimeSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun LoginIn(navController: NavHostController = rememberNavController(), jump: () -> Unit) {
    val viewModel: LARActivityViewModel = viewModel()
    var userName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var btEnabled by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val keyboardController = LocalSoftwareKeyboardController.current

    btEnabled = userName.isNotEmpty() and password.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(viewModel.tip, isError) {
            delay(TimeSpan.LONG)
            viewModel.tip = ""
            isError = false
        }

        Text(
            text = "登录MyChat",
            modifier = Modifier.padding(top = 160.dp),
            color = MaterialTheme.colorScheme.surface,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            letterSpacing = 2.sp
        )

        Text(
            text = viewModel.tip ?: "",
            modifier = Modifier.padding(top = 10.dp),
            color = Color.Red,
            fontSize = 12.sp
        )

        TextField(
            value = userName,
            onValueChange = { userName = it },
            modifier = Modifier.focusRequester(focusRequester),
            label = { Text(text = "用户名", color = Color.Gray) },
            placeholder = { Text(text = "请输入邮箱", color = Color.LightGray) },
            leadingIcon = {
                Image(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = null,
                )
            },
            trailingIcon = {
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
                            }
                            .padding(start = 15.dp, end = 15.dp)
                            .size(20.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                if(!viewModel.checkFormat(userName, 1)) {
                    viewModel.tip = "邮箱格式错误!"
                }
            }),
            shape = RoundedCornerShape(20.dp),
            colors = textFieldColors(
                textColor = Color.Black,
                containerColor = if (isSystemInDarkTheme()) Color.White else Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = if (isSystemInDarkTheme()) Color.Transparent else Color.Gray,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        TextField(
            value = password,
            onValueChange = {
                if (it.length <= 10) {
                    password = it
                } else {
                    viewModel.tip = "超出最长密码长度！"
                }
            },
            modifier = Modifier
                .padding(top = 20.dp)
                .focusRequester(focusRequester),
            isError = isError,
            label = { Text(text = "密码", color = if (isError) Color.Red else Color.Gray) },
            placeholder = {
                Text(
                    text = if (isError) "账号或密码错误输入错误" else "请输入密码",
                    color = if (isError) Color.Red else Color.LightGray,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Image(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                )
            },
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
                            }
                            .padding(start = 15.dp, end = 15.dp)
                            .size(20.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                btEnabled = false
                scope.launch {
                    if (!viewModel.loginIn(userName, password)) {
                        isError = true
                        password = ""
                    }else {
                        jump()
                    }
                }
                keyboardController?.hide()
            }),
            visualTransformation = PasswordVisualTransformation('.'),
            shape = RoundedCornerShape(20.dp),
            colors = textFieldColors(
                textColor = Color.Black,
                containerColor = if (isSystemInDarkTheme()) Color.White else Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = if (isSystemInDarkTheme()) Color.Transparent else Color.Gray,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                errorCursorColor = Color.Black
            ),
            singleLine = true
        )

        Button(
            onClick = {
                scope.launch {
                    if (!viewModel.loginIn(userName, password)) {
                        isError = true
                        password = ""
                    }else {
                        withContext(Dispatchers.Main) {
                            jump()
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(top = 20.dp)
                .width(150.dp),
            enabled = btEnabled,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
                disabledContainerColor = Color.Gray
            )
        ) {
            Text(text = "登录")
        }
        Text(
            text = "还没注册？",
            modifier = Modifier
                .padding(top = 15.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate(RouteConfig.SIGN_PAGE)
                },
            color = Color.Red,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            textDecoration = TextDecoration.Underline
        )
    }
}



