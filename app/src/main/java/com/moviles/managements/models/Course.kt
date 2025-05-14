package com.moviles.managements.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String,

    @ColumnInfo(name = "imageUrl")
    @SerializedName("imageUrl")
    val imageUrl: String,

    @ColumnInfo(name = "schedule")
    @SerializedName("schedule")
    val schedule: String,

    @ColumnInfo(name = "professor")
    @SerializedName("professor")
    val professor: String
)
