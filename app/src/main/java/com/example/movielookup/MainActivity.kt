package com.example.movielookup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.movielookup.models.AppDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var addToDatabaseButton: Button = findViewById(R.id.add_to_database_button)
        var searchMoviesButton: Button = findViewById(R.id.search_movies_button)
        var searchActorsButton: Button = findViewById(R.id.search_actors_button)
        var searchMovieNamesButton: Button = findViewById(R.id.search_movie_names_button)

        var databaseConnection =
            Room.databaseBuilder(this, AppDatabase::class.java, "mydatabase").fallbackToDestructiveMigration().build()

//        Uncomment next 2 lines if you want to delete current database to rebuild
//        databaseConnection.close()
//        this.deleteDatabase("mydatabase")

        // No need for an activity to add the local files to the database
        addToDatabaseButton.setOnClickListener {
            AddToDatabase(this, databaseConnection).main()
        }

        searchMoviesButton.setOnClickListener {
            val searchMoviesIntent = Intent(this, SearchMovies::class.java)
            startActivity(searchMoviesIntent)
        }

        searchActorsButton.setOnClickListener {
            val searchActorsIntent = Intent(this, SearchActors::class.java)
            startActivity(searchActorsIntent)
        }

        searchMovieNamesButton.setOnClickListener {
            val searchMovieNamesIntent = Intent(this, SearchMovieNames::class.java)
            startActivity(searchMovieNamesIntent)
        }
    }
}