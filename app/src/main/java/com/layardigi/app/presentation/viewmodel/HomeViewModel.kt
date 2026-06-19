package com.layardigi.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.layardigi.app.data.model.Movie
import com.layardigi.app.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val allMovies: List<Movie> = emptyList(),
    val searchQuery: String = "",
    val filteredMovies: List<Movie> = emptyList(),
    val isSearching: Boolean = false
) {
    val nowShowingMovies: List<Movie> get() = allMovies.filter { it.isNowShowing }
    val classicMovies: List<Movie> get() = allMovies.filter { !it.isNowShowing }
}

class HomeViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        MovieRepository.moviesFlow,
        _searchQuery,
        _isSearching
    ) { movies, query, searching ->
        val filtered = if (searching && query.isNotBlank()) {
            movies.filter { movie ->
                movie.title.contains(query, ignoreCase = true) ||
                movie.genre.any { it.contains(query, ignoreCase = true) } ||
                movie.director.contains(query, ignoreCase = true)
            }
        } else {
            emptyList()
        }

        HomeUiState(
            allMovies = movies,
            searchQuery = query,
            isSearching = searching,
            filteredMovies = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _isSearching.value = query.isNotBlank()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
    }
}
