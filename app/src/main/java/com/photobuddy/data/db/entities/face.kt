package com.photobuddy.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "face",
    indices = [Index(value = ["photoId"])]
)
data class Face(
    @PrimaryKey(autoGenerate = true) val faceId: Int = 0,
    val title: String,
    val photoId: String,
    val thumbnail: ByteArray,
    val embedding: List<Float>
)



@Entity(
    tableName = "photo_face_cross_ref",
    primaryKeys = ["photoId", "faceId"],
    indices = [
        Index(value = ["photoId"]),
        Index(value = ["faceId"])
    ]
)
data class PhotoFaceCrossRef(
    val photoId: String,
    val faceId: Int
)



// Step 4: Create relationship classes

data class FaceWithPhotos(
    @Embedded val face: Face,
    @Relation(
        parentColumn = "faceId",         // From Face
        entityColumn = "id",             // From PhotoEntity
        associateBy = Junction(
            value = PhotoFaceCrossRef::class,
            parentColumn = "faceId",     // From PhotoFaceCrossRef
            entityColumn = "photoId"     // From PhotoFaceCrossRef
        )
    )
    val photos: List<PhotoEntity>
)





