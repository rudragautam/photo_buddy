package com.photobuddy.data.db.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index(value = ["photoId"])] // ✅ Add this
)
data class FaceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val photoId: String,
    val faceName: String,
    val faceCoordinates: String? = null
)

