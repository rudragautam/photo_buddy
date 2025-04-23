package com.photobuddy.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.photobuddy.data.model.Photo

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val albumId: Int,
    val width: Int?,
    val height: Int?,
    val dateTaken: Long?,
    val description: String?,
    val detectedEmotion: String? = null,
    val capsuleId: String? = null,
    val faceId: String? = null
) {
    fun toPhoto() = Photo(
        id, title, url, thumbnailUrl, albumId, width, height, dateTaken, description,detectedEmotion,capsuleId,faceId
    )

    companion object {
        fun fromPhoto(photo: Photo) = PhotoEntity(
            photo.id, photo.title, photo.url, photo.thumbnailUrl, photo.albumId,
            photo.width, photo.height, photo.dateTaken, photo.description,photo.detectedEmotion,photo.capsuleId,photo.faceId
        )
    }
}
