package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.data.repository.Role
import com.layardigi.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .size(42.dp)
                .background(DarkCard, CircleShape)
        ) {
            Icon(Icons.Rounded.ArrowBackIos, "Kembali", tint = TextPrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    Brush.linearGradient(listOf(CinemaRed, CinemaRedDark)),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Theaters, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Selamat Datang", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Silakan masuk untuk melanjutkan", color = TextSecondary, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMsg = "" },
            label = { Text("Username", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = CinemaRed,
                unfocusedBorderColor = DarkSurfaceVariant,
                containerColor = DarkCard
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMsg = "" },
            label = { Text("Password", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = CinemaRed,
                unfocusedBorderColor = DarkSurfaceVariant,
                containerColor = DarkCard
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMsg, color = CinemaRed, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (username == "admin123" && password == "passadmin") {
                    AuthRepository.login(username, Role.ADMIN)
                    navController.popBackStack()
                } else if (username.isNotBlank() && password.isNotBlank()) {
                    AuthRepository.login(username, Role.USER)
                    navController.popBackStack()
                } else {
                    errorMsg = "Username dan password tidak boleh kosong"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Masuk", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
