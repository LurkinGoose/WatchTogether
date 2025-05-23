package com.example.watch_together.auth

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

@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.user) {
        if (authState.user != null) {
            onAuthSuccess()
        }
    }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isButtonClicked by remember { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
                        .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .clip(CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Назад",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Добро Пожаловать!", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Введите данные для входа в аккаунт",
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Normal)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ошибка с сервера (если есть)
                if (authState.error != null && isButtonClicked && email.isNotBlank() && password.isNotBlank()) {
                    Text(
                        text = authState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Email поле
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (it.isEmpty()) {
                            isButtonClicked = false
                        }},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isButtonClicked && email.isBlank() || isButtonClicked && email.isNotBlank() && emailError != null,
                    supportingText = {
                        if (isButtonClicked && email.isNotBlank() && emailError != null) {
                            Text(
                                text = emailError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.LightGray,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        errorTextColor = Color.Red
                    )
                )

                // Password поле
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it
                        if (it.isEmpty()) {
                            isButtonClicked = false
                        }},
                    label = { Text("Пароль") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        val desc = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                        val tint = if (passwordVisible) Color.DarkGray else Color.Gray

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = desc, tint = tint)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.LightGray,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.DarkGray,
                        focusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    ),
                    isError = isButtonClicked && password.isBlank() || isButtonClicked && password.isNotBlank() && passwordError != null,
                    supportingText = {
                        if (isButtonClicked && password.isNotBlank() && passwordError != null) {
                            Text(
                                text = passwordError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                )

                // Кнопка Войти
                Button(
                    onClick = {
                        isButtonClicked = true
                        emailError = if (InputValidator.isValidEmail(email.trim())) null else "Некорректный email"
                        passwordError = if (InputValidator.isValidPassword(password)) null else "Некорректный пароль"

                        if (emailError == null && passwordError == null) {
                            authViewModel.onIntent(AuthIntent.SignInWithEmail(email.trim(), password.trim()))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Войти",
                        style = TextStyle(color = Color.White, fontSize = 16.sp)
                    )
                }

                // Восстановить пароль
                Text(
                    text = "Восстановить Пароль",
                    modifier = Modifier
                        .clickable { /* Навигация на восстановление */ }
                        .padding(bottom = 8.dp),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )

                // Вход через Google
                Button(
                    onClick = { authViewModel.onIntent(AuthIntent.SignInWithGoogle(context)) },
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
                        style = TextStyle(
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
        
        // Строка с переходом на экран регистрации
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Нет учётной записи? ",
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
            Text(
                text = "Зарегистрироваться",
                modifier = Modifier.clickable {
                    navController.navigate("signUp")
                },
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            )
        }
    }
}
