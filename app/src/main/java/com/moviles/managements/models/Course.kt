package com.moviles.managements.models

import androidx.room.PrimaryKey


data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int?
)
