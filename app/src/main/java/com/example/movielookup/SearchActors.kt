package com.example.movielookup

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.movielookup.models.AppDatabase
import com.example.movielookup.models.Movie
import com.example.movielookup.models.formatMovieText
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchActors : AppCompatActivity() {
    // Only the text result needs to be saved, nothing more
    var searchResultsText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val currentContext = this

        val searchResultsTextView = findViewById<TextView>(R.id.search_results_response)
        val searchBoxInput = findViewById<TextInputEditText>(R.id.search_input)
        val searchActorButton = findViewById<TextView>(R.id.search_button)
        val addToDatabaseButton = findViewById<Button>(R.id.add_search_results_to_db_button)

        // Hide add to database button as its not needed
        addToDatabaseButton.visibility = View.INVISIBLE

        // Add scrolling to the text view
        searchResultsTextView.movementMethod = ScrollingMovementMethod()
        searchBoxInput.hint = "Search for an actor"

        var textToSearch = ""
        val databaseConnection =
            Room.databaseBuilder(this, AppDatabase::class.java, "mydatabase").build()
        val movieDao = databaseConnection.movieDao()

        searchActorButton.setOnClickListener {
            textToSearch = searchBoxInput.text.toString()

            if (textToSearch == "") {
                searchResultsTextView.text = "Nothing to search"
                Toast.makeText(
                    currentContext,
                    "Nothing to search",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var databaseMoviesWithActor = listOf<Movie>()

                runBlocking { launch {
                    databaseMoviesWithActor = movieDao.searchActor("%$textToSearch%")
                } }
                Log.d("DEV_DEBUG", databaseMoviesWithActor.toString())

                var formattedList = mutableListOf<String>()

                // Add the formatted movie string to the array of available actors
                for (f in databaseMoviesWithActor) {
                    formattedList.add(formatMovieText(f))
                }

                // Join the array as a string with spaces in between
                searchResultsText = formattedList.joinToString("\n\n\n\n")
                searchResultsTextView.text = searchResultsText
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search_response", searchResultsText)
        super.onSaveInstanceState(outState)
    }
}