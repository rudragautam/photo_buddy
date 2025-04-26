package com.photobuddy.data.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PhotoWithFaces(
    @Embedded val photo: PhotoEntity,
    @Relation(
        parentColumn = "id",            // PhotoEntity.id
        entityColumn = "faceId",        // Face.faceId
        associateBy = Junction(
            value = PhotoFaceCrossRef::class,
            parentColumn = "photoId",   // From PhotoFaceCrossRef
            entityColumn = "faceId"     // From PhotoFaceCrossRef
        )
    )
    val faces: List<Face>
)





data class FaceWithPhotoCount(
    @Embedded val face: Face,
    @Relation(
        parentColumn = "faceId",
        entityColumn = "faceId", // 👈 should match what's in PhotoFaceCrossRef
        associateBy = Junction(
            value = PhotoFaceCrossRef::class,
            parentColumn = "faceId",
            entityColumn = "photoId"
        )
    )
    val photos: List<PhotoEntity>
) {
    val photoCount: Int
        get() = photos.size
}



