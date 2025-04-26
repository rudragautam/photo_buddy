package com.photobuddy.data.model

data class Photo(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val albumId: String?,
    val width: Int? = null,
    val height: Int? = null,
    val dateTaken: Long? = null,
    val description: String? = null,
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
)

data class AlbumWithImageCountAndRecentImage(
//    val folderName: String,
//    val imageCount: Int,
//    val recentImageUrl: String?

    val folderName: String?="Untittled",
    val albumId: String?="Untittled",
    val imageCount: Int?=0,
    val recentImageUrl: String?="url"
)
