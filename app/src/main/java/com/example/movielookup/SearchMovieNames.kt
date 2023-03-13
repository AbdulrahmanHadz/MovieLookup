package com.example.movielookup

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movielookup.network.APISearch
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class SearchMovieNames : AppCompatActivity() {
    // Class variables for savedInstanceState
    var textToSearch = ""
    var searchResultsText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchResultsTextView = findViewById<TextView>(R.id.search_results_response)
        val searchBoxInput = findViewById<TextInputEditText>(R.id.search_input)
        val addToDatabaseButton = findViewById<Button>(R.id.add_search_results_to_db_button)
        val searchMovieButton = findViewById<TextView>(R.id.search_button)

        // Hide add to database button as it's not needed
        addToDatabaseButton.visibility = View.INVISIBLE

        addToDatabaseButton.setOnClickListener {
            Toast.makeText(this, "That operation is not allowed.", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState != null) {
            searchResultsText = savedInstanceState.getString("search_response") ?: ""
            searchResultsTextView.text = searchResultsText
        }

        val apiSearchObj = APISearch()

        searchMovieButton.setOnClickListener {
            textToSearch = searchBoxInput.text.toString()
            // Search the api with the s parameter for partial search and and type=movie to only search for movies
            var apiSearchData = apiSearchObj.search(mapOf("s" to textToSearch, "type" to "movie"))
            var movieNames = mutableListOf<String>()

            var errorResult = false
            if (apiSearchData != null) {
                // Check if the api returned an error either by no "Search" key being available
                // Or by the "Error" key being available
                errorResult = apiSearchObj.checkError(apiSearchData)
                if (!errorResult) {
                    errorResult = checkError(apiSearchData)
                }
            }

            // Display error message
            if (errorResult || apiSearchData == null) {
                searchResultsText = apiSearchData!!.getString("Error") ?: "Server Error"
                Toast.makeText(this, searchResultsText, Toast.LENGTH_SHORT).show()
            } else {
                // Loop over the JSON Array, format each index as a JSON Object and get only the title
                var searchMovies = apiSearchData.getJSONArray("Search")
                for (movieI in 0 until searchMovies.length()) {
                    var movie = searchMovies.getJSONObject(movieI)
                    // Add title to title array
                    movieNames.add(movie.getString("Title"))
                }
                // Join array as string with newline character separator
                searchResultsText = movieNames.joinToString("\n")
            }

            searchResultsTextView.text = searchResultsText
        }
    }

    // Loop over each key in the array and check if "Search" is in them
    fun checkError(jsonArray: JSONObject): Boolean {
        var error = true
        for (key in jsonArray.keys()) {
            if (key == "Search") {
                error = false
            }
        }
        return error
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Only search response needs to be saved
        outState.putString("search_response", searchResultsText)
        super.onSaveInstanceState(outState)
    }
}