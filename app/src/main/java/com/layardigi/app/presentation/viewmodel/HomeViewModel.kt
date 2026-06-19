package com.layardigi.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val nowShowingMovies: List<Movie> = MovieRepository.nowShowingMovies,
    val classicMovies: List<Movie> = MovieRepository.classicMovies,
    val searchQuery: String = "",
    val filteredMovies: List<Movie> = emptyList(),
    val isSearching: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isSearching = false,
                filteredMovies = emptyList()
            )
        } else {
            val filtered = MovieRepository.allMovies.filter { movie ->
                movie.title.contains(query, ignoreCase = true) ||
                        movie.genre.any { it.contains(query, ignoreCase = true) } ||
                        movie.director.contains(query, ignoreCase = true)
            }
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isSearching = true,
                filteredMovies = filtered
            )
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            isSearching = false,
            filteredMovies = emptyList()
        )
    }
}
