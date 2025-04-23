package com.photobuddy.data.db.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "faces",
    foreignKeys = [
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FaceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val photoId: String, // Foreign key referencing the photo
    val faceName: String, // Name of the person detected
    val faceCoordinates: String? = null // Optional, store face coordinates or bounding box info
)
