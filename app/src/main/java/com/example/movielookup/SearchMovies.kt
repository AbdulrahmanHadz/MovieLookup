package com.example.movielookup

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.movielookup.models.AppDatabase
import com.example.movielookup.models.Movie
import com.example.movielookup.models.formatMovieText
import com.example.movielookup.network.APISearch
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class SearchMovies : AppCompatActivity() {
    // Class variables used in the different OnClickListeners and the savedInstanceState object
    var movieObjFormatted: Movie? = null
    var searchResultsText = ""
    var apiSearchData: JSONObject? = null
    var textToSearch = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val currentContext = this

        val searchResultsTextView = findViewById<TextView>(R.id.search_results_response)
        val searchBoxInput = findViewById<TextInputEditText>(R.id.search_input)
        val addToDataBaseButton = findViewById<Button>(R.id.add_search_results_to_db_button)
        val searchMovieButton = findViewById<TextView>(R.id.search_button)

        // Add scrolling to the textview
        searchResultsTextView.movementMethod = ScrollingMovementMethod()

        val databaseConnection =
            Room.databaseBuilder(this, AppDatabase::class.java, "mydatabase").build()
        val movieDao = databaseConnection.movieDao()

        // Initialise API search obj
        val apiSeaarchObj = APISearch()

        if (savedInstanceState != null) {
            // Get saved text for search results text view
            searchResultsText =
                savedInstanceState.getString("search_response") as String

            // Assign class variable to null or a formatted movie obj depending if something was searched or not
            movieObjFormatted = if (savedInstanceState.getStringArrayList("movie_obj").isNullOrEmpty()) {
                null
            } else {
                movieArrayToObj(savedInstanceState.getStringArrayList("movie_obj") ?: ArrayList())
            }

            // Update the search text view with the saved data
            searchResultsTextView.text = searchResultsText
        }

        searchMovieButton.setOnClickListener {
            textToSearch = searchBoxInput.text.toString().trim()

            if (textToSearch == "") {
                searchResultsText = "Nothing to search."
                Toast.makeText(
                    currentContext,
                    "Nothing to search.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Search the api using the t parameter as the key with the input text as the value
                apiSearchData = apiSeaarchObj.search(mapOf("t" to textToSearch))
                var errorResult = false

                // Api returned an error
                if (apiSearchData != null) {
                    errorResult = apiSeaarchObj.checkError(apiSearchData!!)
                }

                // Display error message
                if (errorResult || apiSearchData == null) {
                    searchResultsText = apiSearchData!!.getString("Error") ?: "Server Error"
                    movieObjFormatted = null
                } else {
                    // Convert the json object into a Movie entity object
                    movieObjFormatted = formatMovieObj(apiSearchData!!)
                    var movieObjString = formatMovieText(movieObjFormatted!!)
                    searchResultsText = movieObjString
                }
            }

            searchResultsTextView.text = searchResultsText
        }

        addToDataBaseButton.setOnClickListener {
            runBlocking {
                launch {

                    if (movieObjFormatted == null) {
                        Toast.makeText(
                            currentContext,
                            "No movie to add.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Search the database to see if this title has already been added
                        if (movieDao.searchTitleExact(movieObjFormatted!!.title) == null) {
                            movieDao.add(movieObjFormatted!!)
                            Toast.makeText(
                                currentContext,
                                "Added to database.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                currentContext,
                                "This title is already in the database.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search_response", searchResultsText)

        // null or the Movie entity can't be saved, format them as an ArrayList to save
        if (movieObjFormatted == null) {
            outState.putStringArrayList("movie_obj", ArrayList<String>())
        } else {
            outState.putStringArrayList("movie_obj", movieObjToArray(movieObjFormatted!!))
        }

        super.onSaveInstanceState(outState)
    }

    // Convert the movie object into an arraylist
    fun movieObjToArray(movieObj: Movie): ArrayList<String> {
        var array = ArrayList<String>()
        array.add(movieObj.title)
        array.add(movieObj.year)
        array.add(movieObj.rated)
        array.add(movieObj.released)
        array.add(movieObj.runtime)
        array.add(movieObj.genre)
        array.add(movieObj.director)
        array.add(movieObj.writer)
        array.add(movieObj.actors)
        array.add(movieObj.plot)
        return array
    }

    // Convert the arraylist into a Movie object
    fun movieArrayToObj(movieArray: ArrayList<String>): Movie {
        return  Movie(
            title = movieArray[0],
            year = movieArray[1],
            rated = movieArray[2],
            released = movieArray[3],
            runtime = movieArray[4],
            genre = movieArray[5],
            director = movieArray[6],
            writer = movieArray[7],
            actors = movieArray[8],
            plot = movieArray[9],
        )
    }

    // Format the json object into a movie object
    fun formatMovieObj(unformattedMovie: JSONObject): Movie {
        var jsonMovie = unformattedMovie
        Log.d("DEV_DEBUG", jsonMovie.toString())
        var title = jsonMovie.getString("Title")
        var year = jsonMovie.getString("Year")
        var rated = jsonMovie.getString("Rated")
        var released = jsonMovie.getString("Released")
        var runtime = jsonMovie.getString("Runtime")
        var genre = jsonMovie.getString("Genre")
        var director = jsonMovie.getString("Director")
        var writer = jsonMovie.getString("Writer")
        var actors = jsonMovie.getString("Actors")
        var plot = jsonMovie.getString("Plot")

        return Movie(
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
    }
}