package com.example.crudmovietiga.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movie (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val desc: String

)
