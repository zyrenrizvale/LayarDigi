package com.layardigi.app.data.repository

import com.layardigi.app.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MovieRepository {

    private val initialNowShowingMovies = listOf(
        Movie(
            id = "colony",
            title = "Colony",
            genre = listOf("Sci-Fi", "Thriller", "Drama"),
            rating = 7.8f,
            duration = 115,
            synopsis = "Di tahun 2092, Bumi telah dikuasai oleh makhluk luar angkasa yang disebut 'The Mesh'. Sekelompok pemberontak manusia berjuang untuk merebut kembali planet mereka dari kolonisasi alien yang kejam. Sam Grant, seorang mantan tentara, memimpin misi berbahaya untuk menghancurkan pusat komando musuh sebelum umat manusia sepenuhnya musnah. Dalam balapan melawan waktu, setiap pilihan menentukan nasib seluruh peradaban.",
            posterUrl = "https://cdn.cgv.id/uploads_v2/movie/compressed/26020800.jpg?version=2",
            isNowShowing = true,
            year = 2026,
            director = "Marcus Webb",
            cast = listOf("Chris Hemsworth", "Ana de Armas", "Michael B. Jordan", "Zoe Saldana"),
            language = "English (Subtitle Indo)",
            ageRating = "17+"
        ),
        Movie(
            id = "peninsula",
            title = "Peninsula",
            genre = listOf("Action", "Horror", "Thriller"),
            rating = 7.5f,
            duration = 116,
            synopsis = "Empat tahun setelah wabah zombie melanda Korea, mantan tentara Jung Seok kembali ke Semenanjung Korea yang kini menjadi zona kematian. Misinya: mengambil kembali kargo senilai jutaan dolar yang terbengkalai di jalan bebas hambatan yang dihuni zombie dan gerombolan manusia liar yang lebih berbahaya dari zombie itu sendiri. Aksi penuh adrenalin di antara reruntuhan peradaban.",
            posterUrl = "https://cdn.cgv.id/uploads/movie/pictures/20011700.jpg?version=2",
            isNowShowing = true,
            year = 2026,
            director = "Yeon Sang-ho",
            cast = listOf("Gang Dong-won", "Lee Jung-hyun", "Lee Re", "Kwon Hae-hyo"),
            language = "Korea (Subtitle Indo)",
            ageRating = "17+"
        ),
        Movie(
            id = "escape",
            title = "Escape",
            genre = listOf("Thriller", "Action", "Mystery"),
            rating = 7.2f,
            duration = 108,
            synopsis = "Seorang wanita muda terjebak di dalam kota yang tiba-tiba dikunci oleh pemerintah akibat ancaman biologi berbahaya. Satu-satunya cara bertahan hidup adalah menemukan jalan keluar sebelum kota dihancurkan dalam 12 jam. Setiap pintu yang dibuka bisa menjadi jebakan, dan setiap orang yang ditemui bisa jadi musuh. Sebuah thriller intens yang tidak memberi ruang untuk bernapas.",
            posterUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQbaPfabrCbx7TOYyD3NZlK68o-BiG1ClHQV_Sk95mn-ttYzSmTTveJ7Fm8&s=10",
            isNowShowing = true,
            year = 2026,
            director = "David Leitch",
            cast = listOf("Florence Pugh", "Tom Hardy", "Oscar Isaac", "Lupita Nyong'o"),
            language = "English (Subtitle Indo)",
            ageRating = "13+"
        )
    )

    private val initialClassicMovies = listOf(
        Movie(
            id = "interstellar",
            title = "Interstellar",
            genre = listOf("Sci-Fi", "Drama", "Adventure"),
            rating = 8.7f,
            duration = 169,
            synopsis = "Ketika Bumi menghadapi kekeringan dan kelaparan massal, mantan pilot NASA Cooper bergabung dengan tim ilmuwan melewati lubang cacing di luar angkasa untuk memastikan kelangsungan hidup umat manusia. Sebuah epik tentang cinta, waktu, dan pengorbanan yang melampaui batas dimensi.",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg",
            isNowShowing = false,
            year = 2014,
            director = "Christopher Nolan",
            cast = listOf("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain", "Michael Caine"),
            language = "English (Subtitle Indo)",
            ageRating = "13+"
        ),
        Movie(
            id = "inception",
            title = "Inception",
            genre = listOf("Sci-Fi", "Action", "Thriller"),
            rating = 8.8f,
            duration = 148,
            synopsis = "Dom Cobb adalah seorang pencuri yang mampu masuk ke dalam mimpi orang lain dan mencuri rahasia dari alam bawah sadar mereka. Ia ditawari kesempatan untuk menghapus catatan kriminalnya dengan melakukan 'inception' — menanamkan ide ke dalam pikiran seseorang. Sebuah perjalanan melalui lapisan-lapisan realita yang mengubah persepsi tentang kebenaran.",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg",
            isNowShowing = false,
            year = 2010,
            director = "Christopher Nolan",
            cast = listOf("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page", "Tom Hardy"),
            language = "English (Subtitle Indo)",
            ageRating = "13+"
        ),
        Movie(
            id = "parasite",
            title = "Parasite",
            genre = listOf("Drama", "Thriller", "Dark Comedy"),
            rating = 8.5f,
            duration = 132,
            synopsis = "Keluarga Ki-taek yang miskin merencanakan cara untuk menyusup ke kehidupan mewah keluarga Park. Rencana yang tampak sempurna ini segera berubah menjadi mimpi buruk yang penuh kejutan gelap tentang ketidaksetaraan kelas sosial yang menghantam dengan telak dan tak terduga.",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BYWZjMjk3ZTItODQ2ZC00NTY5LWE0ZDYtZTI3MjcwN2Q5NTVkXkEyXkFqcGdeQXVyODk4OTc3MTY@._V1_SX300.jpg",
            isNowShowing = false,
            year = 2019,
            director = "Bong Joon-ho",
            cast = listOf("Song Kang-ho", "Lee Sun-kyun", "Cho Yeo-jeong", "Choi Woo-shik"),
            language = "Korea (Subtitle Indo)",
            ageRating = "17+"
        ),
        Movie(
            id = "dark_knight",
            title = "The Dark Knight",
            genre = listOf("Action", "Crime", "Drama"),
            rating = 9.0f,
            duration = 152,
            synopsis = "Batman menghadapi musuh paling berbahaya: The Joker, seorang kriminal anarchis yang bersumpah untuk menciptakan kekacauan di Gotham City. Dalam pertempuran yang menguras jiwa dan raga, Bruce Wayne harus memilih antara menjadi pahlawan yang dibutuhkan atau simbol yang tidak bisa dihancurkan.",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg",
            isNowShowing = false,
            year = 2008,
            director = "Christopher Nolan",
            cast = listOf("Christian Bale", "Heath Ledger", "Aaron Eckhart", "Gary Oldman"),
            language = "English (Subtitle Indo)",
            ageRating = "13+"
        ),
        Movie(
            id = "avengers_endgame",
            title = "Avengers: Endgame",
            genre = listOf("Action", "Sci-Fi", "Adventure"),
            rating = 8.4f,
            duration = 181,
            synopsis = "Setelah Thanos menghapus setengah populasi alam semesta, para Avenger yang tersisa berjuang dengan sisa harapan. Tony Stark, Steve Rogers, dan kawan-kawan menemukan cara untuk memperbaiki kerusakan dalam pertempuran epik yang menentukan nasib seluruh alam semesta.",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_SX300.jpg",
            isNowShowing = false,
            year = 2019,
            director = "Anthony & Joe Russo",
            cast = listOf("Robert Downey Jr.", "Chris Evans", "Mark Ruffalo", "Chris Hemsworth"),
            language = "English (Subtitle Indo)",
            ageRating = "13+"
        )
    )

    private val _moviesFlow = MutableStateFlow<List<Movie>>(initialNowShowingMovies + initialClassicMovies)
    val moviesFlow: StateFlow<List<Movie>> = _moviesFlow.asStateFlow()

    // For backward compatibility before full migration
    val allMovies: List<Movie> get() = _moviesFlow.value
    val nowShowingMovies: List<Movie> get() = _moviesFlow.value.filter { it.isNowShowing }
    val classicMovies: List<Movie> get() = _moviesFlow.value.filter { !it.isNowShowing }

    fun getMovieById(id: String): Movie? = _moviesFlow.value.find { it.id == id }

    fun toggleNowShowing(id: String) {
        _moviesFlow.update { currentList ->
            currentList.map {
                if (it.id == id) it.copy(isNowShowing = !it.isNowShowing) else it
            }
        }
    }

    fun addMovie(movie: Movie) {
        _moviesFlow.update { currentList ->
            currentList + movie
        }
    }

    fun updateMovie(updatedMovie: Movie) {
        _moviesFlow.update { currentList ->
            currentList.map {
                if (it.id == updatedMovie.id) updatedMovie else it
            }
        }
    }
}
