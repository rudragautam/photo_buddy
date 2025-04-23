package com.photobuddy.data.model

data class Photo(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val albumId: Int,
    val width: Int? = null,
    val height: Int? = null,
    val dateTaken: Long? = null,
    val description: String? = null,
    val detectedEmotion: String? = null,
    val capsuleId: String? = null,
    val faceId: String? = null
)

