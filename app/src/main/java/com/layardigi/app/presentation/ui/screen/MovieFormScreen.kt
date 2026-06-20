package com.layardigi.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
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
    var language by remember { mutableStateOf(initialMovie?.language ?: "Indonesia") }
    var ageRating by remember { mutableStateOf(initialMovie?.ageRating ?: "13+") }
    var cast by remember { mutableStateOf(initialMovie?.cast?.joinToString(", ") ?: "") }
    var availableCinemas by remember { mutableStateOf(initialMovie?.availableCinemas?.joinToString(", ") ?: "") }
    var selectedGenres by remember { mutableStateOf(initialMovie?.genre ?: emptyList()) }
    var year by remember { mutableStateOf(initialMovie?.year?.toString() ?: "2026") }

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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    label = "Bahasa", 
                    value = language, 
                    onValueChange = { language = it }, 
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    label = "Batas Usia", 
                    value = ageRating, 
                    onValueChange = { ageRating = it }, 
                    modifier = Modifier.weight(1f)
                )
            }
            
            FormTextField(label = "Pemeran (Pisahkan dengan koma)", value = cast, onValueChange = { cast = it })
            FormTextField(label = "ID Cabang (Pisahkan dengan koma)", value = availableCinemas, onValueChange = { availableCinemas = it })
            FormTextField(label = "Tahun Rilis", value = year, onValueChange = { year = it }, keyboardType = KeyboardType.Number)

            Text("Pilih Genre", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val availableGenres = listOf("Action", "Adventure", "Sci-Fi", "Drama", "Horror", "Thriller", "Comedy", "Romance", "Fantasy", "Animation", "Family")
                availableGenres.forEach { genreName ->
                    val isSelected = selectedGenres.contains(genreName)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CinemaRed.copy(0.2f) else DarkCard,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) CinemaRed else DarkSurfaceVariant
                        ),
                        modifier = Modifier.clickable {
                            selectedGenres = if (isSelected) {
                                selectedGenres - genreName
                            } else {
                                selectedGenres + genreName
                            }
                        }
                    ) {
                        Text(
                            text = genreName,
                            color = if (isSelected) CinemaRed else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
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
                        genre = selectedGenres.ifEmpty { listOf("Action") },
                        rating = rating.toFloatOrNull() ?: 0f,
                        duration = duration.toIntOrNull() ?: 120,
                        synopsis = synopsis.takeIf { it.isNotBlank() } ?: "-",
                        posterUrl = posterUrl.takeIf { it.isNotBlank() } ?: "",
                        isNowShowing = isNowShowing,
                        year = year.toIntOrNull() ?: 2026,
                        director = director.takeIf { it.isNotBlank() } ?: "-",
                        cast = cast.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        language = language.takeIf { it.isNotBlank() } ?: "Indonesia",
                        ageRating = ageRating.takeIf { it.isNotBlank() } ?: "13+",
                        availableCinemas = availableCinemas.split(",").map { it.trim() }.filter { it.isNotEmpty() }
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
