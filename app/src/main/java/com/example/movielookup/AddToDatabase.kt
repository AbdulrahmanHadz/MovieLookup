package com.example.movielookup

import android.util.Log
import android.widget.Toast
import com.example.movielookup.models.AppDatabase
import com.example.movielookup.models.Movie
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray

class AddToDatabase(var context: MainActivity, databaseConnection: AppDatabase) {
    // Setup the movieDao from the database connection
    val movieDao = databaseConnection.movieDao()
    val moviesJson = openJson()

    // Open the locally stored json with the default movies
    fun openJson(): JSONArray {
        // Read the local file and return the data as a JSON Array object
        // Data read from file is read as string
        var jsonString =
            context.assets.open("default_movies.json").bufferedReader().use { it.readText() }
        return JSONArray(jsonString)
    }

    fun getMoviesFromJson(moviesOnDB: List<Movie>): List<Movie> {
        var movies = mutableListOf<Movie>()

        Log.d("DEV_DEBUG", moviesJson.toString())

        // Loop over the JSON Array and format each index as a JSON Object
        for (index in 0 until moviesJson.length()) {
            var jsonMovie = moviesJson.getJSONObject(index)
            var title = jsonMovie.getString("title")
            var year = jsonMovie.getString("year")
            var rated = jsonMovie.getString("rated")
            var released = jsonMovie.getString("released")
            var runtime = jsonMovie.getString("runtime")
            var genre = jsonMovie.getString("genre")
            var director = jsonMovie.getString("director")
            var writer = jsonMovie.getString("writer")
            var actors = jsonMovie.getString("actors")
            var plot = jsonMovie.getString("plot")

            // Initialise a Movie object from the data read
            var movieObject = Movie(
                title = title,
                year = year,
                rated = rated,
                released = released,
                runtime = runtime,
                genre = genre,
                director = director,
                writer = writer,
                actors = actors,
                plot = plot
            )

            var foundMovie = false

            // Check if the title is already in the database
            if (moviesOnDB.isNotEmpty()) {
                for (title in moviesOnDB) {
                    if (title.title != movieObject.title) {
                        foundMovie = true
                    }
                }
            }

            // Add the title to the movies array if not found
            if (!foundMovie) {
                Log.d("DEV_DEBUG", "Added to database: ${movieObject.title}")
                movies.add(movieObject)
            }
        }
        return movies
    }

    fun main() {
        runBlocking {
            launch {
                var moviesOnDB = movieDao.getAll()
                Log.d("DEV_DEBUG", moviesOnDB.toString())
                var movies = getMoviesFromJson(moviesOnDB)
                Log.d("DEV_DEBUG", movies.toString())
                var message = ""

                // Add the movies array to the database
                if (movies.isNotEmpty()) {
                    message = "Added local movies to database."
                    movieDao.add(movies)
                } else {
                    message = "Movies already on database."
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}