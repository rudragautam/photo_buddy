package com.photobuddy.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.db.entities.FaceEntity

data class PhotoWithFaces(
    @Embedded val photo: PhotoEntity,

    @Relation(
        parentColumn = "id",      // This is the PK in PhotoEntity
        entityColumn = "photoId"  // This is the FK in FaceEntity
    )
    val faces: List<FaceEntity>
)
