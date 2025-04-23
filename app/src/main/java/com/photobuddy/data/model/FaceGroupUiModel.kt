package com.photobuddy.data.model

import android.graphics.Bitmap

data class FaceGroupUiModel(
    val groupId: String?=null,
    val faceThumbnail: Bitmap?=null,
    val imagePaths: List<String>?=null
)

/*data class FaceGroup(
    val faceBitmap: Bitmap,
    val imagePaths: List<String>
)*/

data class FaceGroup(
    val groupId: String?=null,
    val representativeEmbedding: FloatArray?=null,
    val faceBitmap: Bitmap?=null,
    val imagePaths: List<String>?=null
)


