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
    val albumId: String,
    val width: Int?,
    val height: Int?,
    val dateTaken: Long?,
    val description: String?,
    val detectedEmotion: String? = null,
    val capsuleId: String? = null,
    val faceId: String? = null,

    // Newly added metadata fields
    val mimeType: String? = null,
    val size: Long? = null,
    val dateAdded: Long? = null,
    val dateModified: Long? = null,
    val orientation: Int? = null,
    val folderName: String? = null,
    val relativePath: String? = null,

    // EXIF metadata
    val cameraMake: String? = null,
    val cameraModel: String? = null,
    val aperture: String? = null,
    val iso: String? = null,
    val exposureTime: String? = null,
    val focalLength: String? = null,
    val whiteBalance: String? = null,
    val flash: String? = null,
    val dateTime: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun toPhoto() = Photo(
        id = id,
        title = title,
        url = url,
        thumbnailUrl = thumbnailUrl,
        albumId = albumId,
        width = width,
        height = height,
        dateTaken = dateTaken,
        description = description,
        detectedEmotion = detectedEmotion,
        capsuleId = capsuleId,
        faceId = faceId,
        mimeType = mimeType,
        size = size,
        dateAdded = dateAdded,
        dateModified = dateModified,
        orientation = orientation,
        folderName = folderName,
        relativePath = relativePath,
        cameraMake = cameraMake,
        cameraModel = cameraModel,
        aperture = aperture,
        iso = iso,
        exposureTime = exposureTime,
        focalLength = focalLength,
        whiteBalance = whiteBalance,
        flash = flash,
        dateTime = dateTime,
        latitude = latitude,
        longitude = longitude
    )

    companion object {
        fun fromPhoto(photo: Photo) = PhotoEntity(
            id = photo.id,
            title = photo.title,
            url = photo.url,
            thumbnailUrl = photo.thumbnailUrl,
            albumId = photo.albumId!!,
            width = photo.width,
            height = photo.height,
            dateTaken = photo.dateTaken,
            description = photo.description,
            detectedEmotion = photo.detectedEmotion,
            capsuleId = photo.capsuleId,
            faceId = photo.faceId,
            mimeType = photo.mimeType,
            size = photo.size,
            dateAdded = photo.dateAdded,
            dateModified = photo.dateModified,
            orientation = photo.orientation,
            folderName = photo.folderName,
            relativePath = photo.relativePath,
            cameraMake = photo.cameraMake,
            cameraModel = photo.cameraModel,
            aperture = photo.aperture,
            iso = photo.iso,
            exposureTime = photo.exposureTime,
            focalLength = photo.focalLength,
            whiteBalance = photo.whiteBalance,
            flash = photo.flash,
            dateTime = photo.dateTime,
            latitude = photo.latitude,
            longitude = photo.longitude
        )
    }
}
