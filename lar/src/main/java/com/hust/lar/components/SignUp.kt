package com.hust.lar.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.hust.lar.R
import com.hust.lar.viewmodels.LARActivityViewModel
import com.hust.resbase.TimeSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun SignUp(navController: NavHostController) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("")}
    var passwordRepeat by remember { mutableStateOf("") }
    var btEnabled by remember { mutableStateOf(false) }
    val mediaAction by lazy { PhotoComponent.instance }
    var localImgPath by remember{ mutableStateOf(Uri.EMPTY) }

    val viewModel: LARActivityViewModel = viewModel()
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current


    btEnabled = userName.isNotEmpty() and password.isNotEmpty() and passwordRepeat.isNotEmpty() and (localImgPath != Uri.EMPTY)
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        val (rPic, rTitle, rTip, rNickname, rUserName, rPassword, rPasswordRepeat, rBt) = createRefs()

        LaunchedEffect(viewModel.tip) {
            delay(TimeSpan.LONG)
            viewModel.tip = ""
        }
        Box(
            modifier = Modifier
                .constrainAs(rPic) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 80.dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground)
                .clickable {
                    // Launch the image picker when the box is clicked
                    mediaAction.selectImage()
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = localImgPath, contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = localImgPath == Uri.EMPTY,
                        color = Color(231, 234, 239, 255),
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                contentScale = ContentScale.Crop
            )
            mediaAction.Register(
                galleryCallback = {
                    if (it.isSuccess) {
                        it.uri?.let { uri ->
                            localImgPath = viewModel.createCopyAndReturnRealPath(context, uri)
                        }
                    }
                },
                permissionRationale = {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Text(
            text = "注册MyChat",
            modifier = Modifier
                .constrainAs(rTitle) {
                    top.linkTo(rPic.bottom)
                    start.linkTo(rPic.start)
                    end.linkTo(rPic.end)
                }
                .padding(top = 20.dp),
            color = MaterialTheme.colorScheme.surface,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            letterSpacing = 2.sp
        )

        Text(
            text = viewModel.tip,
            modifier = Modifier
                .constrainAs(rTip) {
                    top.linkTo(rTitle.bottom)
                    start.linkTo(rTitle.start)
                    end.linkTo(rTitle.end)
                }
                .padding(top = 10.dp),
            color = Color.Red,
            fontSize = 12.sp
        )

        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier
                .constrainAs(rNickname) {
                    top.linkTo(rTip.bottom)
                    start.linkTo(rTip.start)
                    end.linkTo(rTip.end)
                }
                .padding(top = 15.dp)
                .focusRequester(focusRequester),
            label = { Text(text = "昵称") },
            placeholder = { Text(text = "请输入昵称", color = Color.LightGray) },
            trailingIcon = {
                AnimatedVisibility(
                    visible = nickname.isNotEmpty(),
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Image(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                nickname = ""
                            }
                            .padding(start = 15.dp, end = 15.dp)
                            .size(20.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
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
            value = userName,
            onValueChange = { userName = it },
            modifier = Modifier
                .constrainAs(rUserName) {
                    top.linkTo(rNickname.bottom)
                    start.linkTo(rNickname.start)
                    end.linkTo(rNickname.end)
                }
                .padding(top = 15.dp)
                .focusRequester(focusRequester),
            label = { Text(text = "用户名") },
            placeholder = { Text(text = "请输入邮箱", color = Color.LightGray) },
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
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
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
                .constrainAs(rPassword) {
                    top.linkTo(rUserName.bottom)
                    start.linkTo(rUserName.start)
                    end.linkTo(rUserName.end)
                }
                .padding(top = 20.dp)
                .focusRequester(focusRequester),
            label = { Text(text = "密码") },
            placeholder = {
                Text(
                    text = "请输入密码",
                    fontSize = 13.sp
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
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation('.'),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
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

        TextField(
            value = passwordRepeat,
            onValueChange = {
                if (it.length <= 10) {
                    passwordRepeat = it
                } else {
                    viewModel.tip = "超出最长密码长度！"
                }
            },
            modifier = Modifier
                .constrainAs(rPasswordRepeat) {
                    top.linkTo(rPassword.bottom)
                    start.linkTo(rPassword.start)
                    end.linkTo(rPassword.end)
                }
                .padding(top = 20.dp)
                .focusRequester(focusRequester),
            label = { Text(text = "确认密码") },
            placeholder = {
                Text(
                    text = "请再次输入密码",
                    fontSize = 13.sp
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = passwordRepeat.isNotEmpty(),
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Image(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                passwordRepeat = ""
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
                scope.launch {
                    if(viewModel.signUp(userName, password, nickname, localImgPath)) {
                        withContext(Dispatchers.Main) {
                            navController.popBackStack()
                        }
                    }
                }
                keyboardController?.hide()
            }),
            visualTransformation = PasswordVisualTransformation('.'),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
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

        TextButton(
            onClick = {
                if (password != passwordRepeat) {
                    passwordRepeat = ""
                    viewModel.tip = "确认密码与第一次输入不符"
                } else {
                    scope.launch {
                        if(viewModel.signUp(userName, password, nickname, localImgPath)) {
                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .constrainAs(rBt) {
                    top.linkTo(rPasswordRepeat.bottom)
                    start.linkTo(rPasswordRepeat.start)
                    end.linkTo(rPasswordRepeat.end)
                }
                .padding(top = 20.dp)
                .width(150.dp),
            enabled = btEnabled,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(R.color.teal_200),
                disabledContainerColor = Color.Gray
            )
        ) {
            Text(text = "注册")
        }
    }
}