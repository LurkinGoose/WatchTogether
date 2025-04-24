package com.example.watch_together.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.watch_together.R
import com.example.watch_together.viewModels.AuthViewModel

@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel, onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    val authState by viewModel.state.collectAsState()

    LaunchedEffect(authState.user) {
        if (authState.user != null) {
            onAuthSuccess()
        }
    }

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isButtonClicked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clip(CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Назад",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Регистрация", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Введите данные для создания аккаунта",
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                if (authState.error != null && isButtonClicked && name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    Text(
                        text = authState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isButtonClicked && name.isBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.LightGray,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isButtonClicked && email.isBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.LightGray,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )

                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        val description = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                        val tint = if (passwordVisible) Color.DarkGray else Color.Gray

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description, tint = tint)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = isButtonClicked && password.isBlank(),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.LightGray,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )

                Button(
                    onClick = {
                        isButtonClicked = true
                        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signUpWithEmail(name.trim(), email.trim(), password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Зарегистрироваться",
                        style = TextStyle(color = Color.White, fontSize = 18.sp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.signInWithGoogle(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )

                    Text(
                        text = "Войти с помощью Google",
                        style = TextStyle(color = Color.DarkGray, fontSize = 18.sp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Уже есть аккаунт? ",
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = "Войдите",
                modifier = Modifier.clickable {
                    navController.navigate("signIn")
                },
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            )
        }
    }
}
