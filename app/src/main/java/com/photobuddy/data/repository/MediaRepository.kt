/*
package com.photobuddy.data.repository

import android.content.Context
import android.provider.MediaStore
import com.photobuddy.data.model.Media
import com.photobuddy.data.model.Photo
import com.photobuddy.data.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository(
    private val mediaDao: MediaDao,
    private val context: Context
) {
    suspend fun getAllMedia(): List<Media> {
        return withContext(Dispatchers.IO) {
            mediaDao.getAllMedia().map { it.toMedia() }
        }
    }

    suspend fun refreshMedia() {
        withContext(Dispatchers.IO) {
            val photos = fetchPhotosFromDevice()
            val videos = fetchVideosFromDevice()
            mediaDao.deleteAll()
            mediaDao.insertAll((photos + videos).map { MediaEntity.fromMedia(it) })
        }
    }

    private suspend fun fetchPhotosFromDevice(): List<Photo> {
        return withContext(Dispatchers.IO) {
            val photos = mutableListOf<Photo>()
            val contentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.ORIENTATION
            )

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    photos.add(
                        Photo(
                            id = cursor.getString(0),
                            title = cursor.getString(1),
                            path = cursor.getString(2),
                            dateTaken = cursor.getLong(3),
                            size = cursor.getLong(4),
                            width = cursor.getInt(5),
                            height = cursor.getInt(6),
                            orientation = cursor.getInt(7)
                        )
                    )
                }
            }

            photos
        }
    }

    private suspend fun fetchVideosFromDevice(): List<Video> {
        return withContext(Dispatchers.IO) {
            val videos = mutableListOf<Video>()
            val contentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION
            )

            contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_TAKEN} DESC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    videos.add(
                        Video(
                            id = cursor.getString(0),
                            title = cursor.getString(1),
                            path = cursor.getString(2),
                            dateTaken = cursor.getLong(3),
                            size = cursor.getLong(4),
                            width = cursor.getInt(5),
                            height = cursor.getInt(6),
                            duration = cursor.getLong(7)
                        )
                    )
                }
            }

            videos
        }
    }
}
*/
