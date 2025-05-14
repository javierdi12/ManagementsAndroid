package com.moviles.managements.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "Students", //Name of the table in the BD
    foreignKeys = [
        ForeignKey(
            entity = Course::class,  //Entity being referred to
            parentColumns = ["id"],//column primary in the table Course
            childColumns = ["courseId"], // column of this entity that refers to Course
            onDelete = ForeignKey.CASCADE // if the course is deleted, their tasks are deleted
        )
    ],
    indices = [
        Index(value = ["courseId"])  // index to avoid full scans
    ]
)

data class Student(
    @PrimaryKey(autoGenerate = true) var id: Int?, //primary key
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "courseId") val courseId: Int? // ForeignKey
)
