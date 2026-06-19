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
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.repository.MovieRepository
import com.layardigi.app.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieFormScreen(navController: NavController, movieId: String?) {
    val isEditMode = movieId != null
    val initialMovie = if (isEditMode) MovieRepository.getMovieById(movieId!!) else null

    var title by remember { mutableStateOf(initialMovie?.title ?: "") }
    var synopsis by remember { mutableStateOf(initialMovie?.synopsis ?: "") }
    var posterUrl by remember { mutableStateOf(initialMovie?.posterUrl ?: "") }
    var rating by remember { mutableStateOf(initialMovie?.rating?.toString() ?: "") }
    var duration by remember { mutableStateOf(initialMovie?.duration?.toString() ?: "") }
    var director by remember { mutableStateOf(initialMovie?.director ?: "") }
    var isNowShowing by remember { mutableStateOf(initialMovie?.isNowShowing ?: true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(42.dp)
                    .background(DarkCard, CircleShape)
            ) {
                Icon(Icons.Rounded.ArrowBackIos, "Kembali", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (isEditMode) "Edit Film" else "Tambah Film",
                    color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(label = "Judul Film", value = title, onValueChange = { title = it })
            FormTextField(label = "Poster URL", value = posterUrl, onValueChange = { posterUrl = it })
            FormTextField(label = "Sutradara", value = director, onValueChange = { director = it })
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    label = "Rating (0-10)", 
                    value = rating, 
                    onValueChange = { rating = it }, 
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Decimal
                )
                FormTextField(
                    label = "Durasi (menit)", 
                    value = duration, 
                    onValueChange = { duration = it }, 
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }

            FormTextField(
                label = "Sinopsis", 
                value = synopsis, 
                onValueChange = { synopsis = it }, 
                singleLine = false,
                modifier = Modifier.height(150.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                Text("Sedang Tayang", color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = isNowShowing,
                    onCheckedChange = { isNowShowing = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CinemaRed,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DarkSurfaceVariant
                    )
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
                    val newMovie = Movie(
                        id = movieId ?: UUID.randomUUID().toString(),
                        title = title.takeIf { it.isNotBlank() } ?: "Untitled",
                        genre = initialMovie?.genre ?: listOf("Action"), // simplified
                        rating = rating.toFloatOrNull() ?: 0f,
                        duration = duration.toIntOrNull() ?: 120,
                        synopsis = synopsis.takeIf { it.isNotBlank() } ?: "-",
                        posterUrl = posterUrl.takeIf { it.isNotBlank() } ?: "",
                        isNowShowing = isNowShowing,
                        year = initialMovie?.year ?: 2026,
                        director = director.takeIf { it.isNotBlank() } ?: "-",
                        cast = initialMovie?.cast ?: emptyList(),
                        language = initialMovie?.language ?: "English",
                        ageRating = initialMovie?.ageRating ?: "13+"
                    )
                    
                    if (isEditMode) {
                        MovieRepository.updateMovie(newMovie)
                    } else {
                        MovieRepository.addMovie(newMovie)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Simpan Data", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = CinemaRed,
            unfocusedBorderColor = DarkSurfaceVariant,
            containerColor = DarkCard
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
