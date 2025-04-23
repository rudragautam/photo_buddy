package com.photobuddy.data.model

data class Video(
     val id: String,
     val title: String,
     val path: String,
     val dateTaken: Long,
     val size: Long,
     val width: Int?,
     val height: Int?,
    val duration: Long
)