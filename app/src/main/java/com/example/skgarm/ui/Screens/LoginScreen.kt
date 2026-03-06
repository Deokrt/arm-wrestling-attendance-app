package com.example.skgarm.ui.Screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skgarm.ui.theme.BgField
import com.example.skgarm.ui.theme.BgPrimary
import com.example.skgarm.ui.theme.ErrorRed
import com.example.skgarm.ui.theme.Teal
import com.example.skgarm.ui.theme.TextPrimary
import com.example.skgarm.ui.theme.TextSecondary

import com.example.skgarm.Viewmodel.AppViewModel
import com.example.skgarm.Viewmodel.AuthState

@Composable
fun LoginScreen(
    viewModel: AppViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var isJoinMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val isLoading = authState is AuthState.Loading

    fun submit() {
        focusManager.clearFocus()
        if (isJoinMode) viewModel.register(name, email, password)
        else viewModel.signIn(email, password)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo icon
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Teal,
                modifier = Modifier.size(76.dp),
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "Skg Arm",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Text(
                "Training Scheduler",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(40.dp))

            // Name field (join mode only)
            AnimatedVisibility(visible = isJoinMode) {
                Column {
                    ArmTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Full Name",
                        imeAction = ImeAction.Next,
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            ArmTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                placeholder = "Email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
            Spacer(Modifier.height(12.dp))

            ArmTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                placeholder = "Password",
                isPassword = true,
                imeAction = ImeAction.Done,
                onNext = { submit() }
            )

            // Error message
            AnimatedVisibility(visible = authState is AuthState.Error) {
                Text(
                    (authState as? AuthState.Error)?.message ?: "",
                    color = ErrorRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Primary button
            Button(
                onClick = { submit() },
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (isJoinMode) "Join Team" else "Sign In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Secondary button
            Button(
                onClick = {
                    isJoinMode = !isJoinMode
                    viewModel.clearError()
                    name = ""; email = ""; password = ""
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BgField),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    if (isJoinMode) "Already have an account? Sign In" else "Join Team",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ArmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextSecondary) },
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onNext() },
            onDone = { onNext() }
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BgField,
            unfocusedContainerColor = BgField,
            focusedBorderColor = Teal,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Teal
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
