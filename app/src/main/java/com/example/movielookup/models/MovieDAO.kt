package com.example.movielookup.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovieDAO {
    @Query("SELECT * FROM movies")
    suspend fun getAll(): List<Movie>

    @Insert
    suspend fun add(vararg movie: Movie)

    @Insert
    suspend fun add(movies: List<Movie>)

    @Query("SELECT * FROM movies WHERE LOWER(movie_title) LIKE LOWER(:title)")
    suspend fun searchTitle(title: String): List<Movie>

    @Query("SELECT * FROM movies WHERE LOWER(movie_title)=LOWER(:title)")
    suspend fun searchTitleExact(title: String): Movie?

    @Query("SELECT * FROM movies WHERE LOWER(movie_actors) LIKE LOWER(:actor)")
    suspend fun searchActor(actor: String): List<Movie>
}

// Format the movie object as a string
fun formatMovieText(movieObj: Movie): String {
    return """
            Title: ${movieObj.title}
            Year: ${movieObj.year}
            Rated: ${movieObj.rated}
            Released: ${movieObj.released}
            Runtime: ${movieObj.runtime}
            Genre: ${movieObj.genre}
            Director: ${movieObj.director}
            Writer: ${movieObj.writer}
            Actors: ${movieObj.actors}
            
            Plot: ${movieObj.plot}
        """.trimIndent()
}