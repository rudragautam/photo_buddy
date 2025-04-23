package com.photobuddy.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.FaceDetector
import com.photobuddy.data.db.AppDatabase
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.model.FaceGroupUiModel
import com.photobuddy.data.model.Photo
import com.photobuddy.data.repository.PhotoRepository
import com.photobuddy.utils.tflitehelper.FaceGroupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PhotoRepository
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

    init {
        val photoDao = AppDatabase.getInstance(application).photoDao()
        repository = PhotoRepository(photoDao, application)
    }

    fun checkPermissionsAndLoadPhotos(hasPermission: Boolean) {
        _permissionGranted.value = hasPermission
        if (hasPermission) {
            loadPhotos()
        } else {
            _error.value = "Storage permission required to access photos"
        }
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val photos = withContext(Dispatchers.IO) {
                    // First try to get from local DB
                    val localPhotos = repository.getAllPhotos()
                    if (localPhotos.isNotEmpty()) {
                        return@withContext localPhotos
                    }

                    // If no local photos, fetch from device storage
                    fetchPhotosFromDeviceStorage()
                }

                _photos.value = photos
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load photos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchPhotosFromDeviceStorage(): List<Photo> {
        return withContext(Dispatchers.IO) {
            val photos = mutableListOf<Photo>()
            val context = getApplication<Application>().applicationContext
            val contentResolver: ContentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.SIZE
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val widthColumn = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getString(idColumn)
                    val name = cursor.getString(nameColumn)
                    val path = cursor.getString(dataColumn)
                    val dateTaken = cursor.getLong(dateColumn)
                    val width = if (widthColumn != -1) cursor.getInt(widthColumn) else null
                    val height = if (heightColumn != -1) cursor.getInt(heightColumn) else null

                    photos.add(
                        Photo(
                            id = id,
                            title = name,
                            url = path,
                            thumbnailUrl = path, // Using same path for thumbnail
                            albumId = 0, // Not applicable for device photos
                            width = width,
                            height = height,
                            dateTaken = dateTaken,
                            description = null
                        )
                    )
                }
            }

            // Cache the photos in local DB
            if (photos.isNotEmpty()) {
                repository.refreshPhotos(photos)
            }

            photos
        }
    }

    fun insert(photo: PhotoEntity) = viewModelScope.launch {
        repository.insertPhoto(photo)
    }

    fun refreshPhotos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newPhotos = fetchPhotosFromDeviceStorage()
                _photos.value = newPhotos
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to refresh photos"
            } finally {
                _isLoading.value = false
            }
        }
    }


/*    fun getFaceGroupsAsUiModel(): List<FaceGroupUiModel> {
        return faceGroups.mapIndexed { index, group ->
            FaceGroupUiModel(
                groupId = "Person ${index + 1}",
                faceThumbnail = group.faceBitmap,
                imagePaths = group.imagePaths
            )
        }
    }*/

    suspend fun detectFacesAndSave(photo: PhotoEntity, detectedFaces: List<String>) {
        // First, insert the photo into the database if it isn't already present
//        photoDao.insertPhoto(photo)

        // Now, for each detected face, insert into the faces table
        detectedFaces.forEach { faceName ->
            val faceEntity = FaceEntity(
                photoId = photo.id, // Link face to the photo using photoId
                faceName = faceName
            )
            repository.insertFace(faceEntity)
        }
    }

    fun groupFacesFromAllPhotos() {
        viewModelScope.launch {
            val allPhotos = repository.getAllPhotos() // Fetch all photos from DB

            val photoEntities = allPhotos.map { photo ->
                PhotoEntity(
                    id = photo.id,
                    url = photo.url,
                    title = photo.title,
                    faceId = photo.faceId,
                    width = photo.width,
                    height = photo.height,
                    albumId = photo.albumId,
                    thumbnailUrl = photo.thumbnailUrl,
                    dateTaken = photo.dateTaken,
                    description = photo.description,
                )
            }

            val faceGroupManager = FaceGroupManager(getApplication())

            val faceGroups = faceGroupManager.groupSimilarFaces(photoEntities) // Your face grouping logic

            faceGroups.forEach { (faceEntity, photoList) ->
                val faceId = UUID.randomUUID().toString()
                val newFaceEntity = faceEntity.copy(id = faceId)
                repository.insertFace(newFaceEntity)

                photoList.forEach { photo ->
                    val updatedPhoto = photo.copy(faceId = faceId)
                    repository.updatePhoto(updatedPhoto)
                }
            }
        }
    }




    // PhotoViewModel.kt
    private val _groupedPhotos = MutableLiveData<List<PhotoWithFaceGroup>>()
    val groupedPhotos: LiveData<List<PhotoWithFaceGroup>> = _groupedPhotos

    fun loadGroupedPhotos() {
        viewModelScope.launch {
            val faceGroups = repository.getGroupedFacesWithPhotos()
            _groupedPhotos.postValue(faceGroups)
        }
    }


}

data class PhotoWithFaceGroup(
    val faceName: String,
    val faceId: String,
    val photos: List<PhotoEntity>
)
