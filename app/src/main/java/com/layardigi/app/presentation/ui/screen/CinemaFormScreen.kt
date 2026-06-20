package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.layardigi.app.data.model.Cinema
import com.layardigi.app.data.repository.CinemaRepository
import com.layardigi.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaFormScreen(navController: NavController, cinemaId: String?) {
    val isEditMode = cinemaId != null
    val initialCinema = if (isEditMode) CinemaRepository.getCinemaById(cinemaId!!) else null

    var name by remember { mutableStateOf(initialCinema?.name ?: "") }
    var address by remember { mutableStateOf(initialCinema?.address ?: "") }
    var city by remember { mutableStateOf(initialCinema?.city ?: "") }
    var basePrice by remember { mutableStateOf(initialCinema?.basePrice?.toString() ?: "55000") }
    var latitude by remember { mutableStateOf(initialCinema?.latitude?.toString() ?: "0.0") }
    var longitude by remember { mutableStateOf(initialCinema?.longitude?.toString() ?: "0.0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(label = "Nama Cabang", value = name, onValueChange = { name = it })
            FormTextField(label = "Alamat Lengkap", value = address, onValueChange = { address = it })
            FormTextField(label = "Kota", value = city, onValueChange = { city = it })
            
            FormTextField(
                label = "Harga Tiket Dasar (Rp)", 
                value = basePrice, 
                onValueChange = { basePrice = it }, 
                keyboardType = KeyboardType.Number
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    label = "Latitude", 
                    value = latitude, 
                    onValueChange = { latitude = it }, 
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Decimal
                )
                FormTextField(
                    label = "Longitude", 
                    value = longitude, 
                    onValueChange = { longitude = it }, 
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Decimal
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Save Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard)
                .padding(20.dp)
        ) {
            Button(
                onClick = {
                    val newId = cinemaId ?: "ld_${name.lowercase().replace(" ", "_")}"
                    val cinemaData = mapOf(
                        "id" to newId,
                        "name" to (name.takeIf { it.isNotBlank() } ?: "Untitled"),
                        "address" to (address.takeIf { it.isNotBlank() } ?: "-"),
                        "city" to (city.takeIf { it.isNotBlank() } ?: "-"),
                        "latitude" to (latitude.toDoubleOrNull() ?: 0.0),
                        "longitude" to (longitude.toDoubleOrNull() ?: 0.0),
                        "basePrice" to (basePrice.toIntOrNull() ?: 55000),
                        "showtimes" to (initialCinema?.showtimes ?: listOf("10:00", "12:30", "15:00", "18:00", "20:30"))
                    )
                    
                    if (isEditMode) {
                        CinemaRepository.updateCinema(cinemaData)
                    } else {
                        CinemaRepository.addCinema(cinemaData)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Simpan Cabang", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
