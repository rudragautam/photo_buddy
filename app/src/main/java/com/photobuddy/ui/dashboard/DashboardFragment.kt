package com.photobuddy.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.photobuddy.databinding.FragmentDashboardBinding
import com.photobuddy.ui.adapter.GroupedFaceAdapter
import com.photobuddy.ui.adapter.PhotoWithFaceGroupAdapter
import com.photobuddy.utils.tflitehelper.FaceGroupManager
import com.photobuddy.viewmodel.PhotoViewModel
import java.util.*

class DashboardFragment : Fragment() {

    private val photoViewModel: PhotoViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var faceGroupManager: FaceGroupManager
    private lateinit var faceDetector: FaceDetector
//    private lateinit var adapter: PeopleAdapter

    // Activity result launcher to pick images
/*
    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.size == 2) {
            // Pick two images
            val firstUri = uris[0]
            val secondUri = uris[1]

            // Process the selected images
            loadImageAndDetectFaces(firstUri)
            loadImageAndDetectFaces(secondUri)
        } else {
            Toast.makeText(requireContext(), "Please select exactly two images", Toast.LENGTH_SHORT).show()
        }
    }
*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Face Detector
        faceDetector = FaceDetection.getClient()

        // Initialize Face Group Manager
        faceGroupManager = FaceGroupManager(requireActivity())

        photoViewModel.groupFacesFromAllPhotos()

        // Initialize Adapter
        /*adapter = PeopleAdapter(emptyList()) { faceGroup ->
            // Handle face group click (optional)
        }*/

        // Set up RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val adapter = PhotoWithFaceGroupAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

// Suppose `faceGroups: Map<FaceEntity, List<PhotoEntity>>` is your data:

//        binding.recyclerView.adapter = adapter


        // In your Fragment or Activity
        photoViewModel.groupedPhotos.observe(viewLifecycleOwner) { groupedList ->
            adapter.submitList(groupedList)
//            binding.recyclerView.adapter = GroupedFaceAdapter(groupedList)
        }

// Trigger load
        photoViewModel.loadGroupedPhotos()
        return root

    }

}

        // Set a button click listener to launch image picker
       /* *//*binding.pickImagesButton.setOnClickListener {
            getImageLauncher.launch("image/*")
        }*//*

        return root
    }

/*    private fun loadImageAndDetectFaces(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            detectFacesInImage(bitmap, imageUri)
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error loading image", e)
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun detectFacesInImage(bitmap: Bitmap, imageUri: Uri) {
        val image = InputImage.fromBitmap(bitmap, 0)

        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                // Loop through detected faces
                faces.forEach { face ->
                    val faceBitmap = cropFaceFromBitmap(bitmap, face.boundingBox)
                    val faceGroup = FaceGroup(
                        faceBitmap = faceBitmap, imagePaths = listOf(imageUri.toString()),
                        groupId = null,
                        representativeEmbedding = null
                    )

                    // Add the detected face to the face group manager
                    faceGroupManager.addFaceGroup(faceGroup)
                }

                // Once faces are detected and grouped, update the UI
                updateUI()
            }
            .addOnFailureListener { exception ->
                Log.e("DashboardFragment", "Face detection failed", exception)
                Toast.makeText(requireContext(), "Face detection failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cropFaceFromBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }*//*

    private fun updateUI() {
        // Get the face groups as UI models
        val faceGroups = faceGroupManager.getFaceGroupsAsUiModel()

        if (faceGroups.isNotEmpty()) {
            // Update the RecyclerView adapter with the new face groups
            adapter.updateData(faceGroups)
        } else {
            Toast.makeText(requireContext(), "No faces detected", Toast.LENGTH_SHORT).show()
        }

        // Example to update some status or text based on the face group data
        binding.textStatus.text = "Faces detected: ${faceGroups.size}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
*/