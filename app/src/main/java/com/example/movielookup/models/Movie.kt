package com.example.movielookup.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "movie_title") val title: String,
    @ColumnInfo(name = "movie_year") val year: String,
    @ColumnInfo(name = "movie_rated") val rated: String,
    @ColumnInfo(name = "movie_released") val released: String,
    @ColumnInfo(name = "movie_runtime") val runtime: String,
    @ColumnInfo(name = "movie_genre") val genre: String,
    @ColumnInfo(name = "movie_director") val director: String,
    @ColumnInfo(name = "movie_writer") val writer: String,
    @ColumnInfo(name = "movie_actors") val actors: String,
    @ColumnInfo(name = "movie_plot") val plot: String
)
