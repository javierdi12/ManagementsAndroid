package com.moviles.managements.models

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithCourse(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "courseId",
        entityColumn = "id"
    )
    val course: Course?
)