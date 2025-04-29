package com.photobuddy.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photobuddy.databinding.FragmentHomeBinding
import com.photobuddy.ui.adapter.AlbumAdapter
import com.photobuddy.ui.adapter.FaceAdapter
import com.photobuddy.ui.adapter.LibraryAdapter
import com.photobuddy.ui.adapter.PhotoAdapter
import com.photobuddy.utils.visible
import com.photobuddy.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
//    private val photoAdapter = PhotoAdapter()
    private val libraryAdapter = LibraryAdapter()
    private val albumAdapter = AlbumAdapter()
    private val faceAdapter = FaceAdapter()

    private val photoViewModel: PhotoViewModel by viewModels()

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        photoViewModel.checkPermissionsAndLoadPhotos(isGranted)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        checkPermissions()
//        binding.swipeRefreshLayout.isRefreshing = false
//        setupRefreshListener()
    }

    private fun setupRecyclerView() {
        binding.photoRecyclerView.apply {
            adapter = faceAdapter
            layoutManager=LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        binding.albumRecyclerView.apply {
             layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumAdapter
        }

        binding.libraryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = libraryAdapter
        }


        /*val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView( binding.photoRecyclerView)*/

    }

    private fun observeViewModel() {
        photoViewModel.loadAlbumsWithImageCount()
        lifecycleScope.launch {
            photoViewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visible(isLoading)
                binding.photoRecyclerView.visible(!isLoading)
            }
        }

        lifecycleScope.launch {
            /*photoViewModel.photos.collectLatest { photos ->
                photoAdapter.submitList(photos)
                binding.emptyState.visible(photos.isEmpty())
            }*/
        }

        lifecycleScope.launch {
            photoViewModel.error.collectLatest { error ->
                error?.let {
//                    showErrorSnackbar(it)
//                    photoViewModel.clearError()
                }
            }
        }

        lifecycleScope.launch {
            photoViewModel.permissionGranted.collectLatest { granted ->
                if (!granted) {
                    showPermissionExplanation()
                }
            }
        }




        lifecycleScope.launch {
            photoViewModel.albumsWithImageCount.observe(requireActivity(), Observer { albums ->
                albumAdapter.submitList(albums)
                libraryAdapter.submitList(albums)
                binding.albumRecyclerView.adapter = albumAdapter
            })
        }

        photoViewModel.faces.observe(viewLifecycleOwner) { faces ->
            faceAdapter.submitList(faces)
        }

        photoViewModel.loadAllFaces()
    }

    private fun checkPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                photoViewModel.checkPermissionsAndLoadPhotos(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionExplanation()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionExplanation() {
       /* Snackbar.make(
            binding.root,
            "Photo access permission is required to display your photos",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Grant") {
            requestPermissionLauncher.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
            )
        }.show()*/
    }

    private fun showErrorSnackbar(message: String) {
//        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

//    private fun setupRefreshListener() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            photoViewModel.refreshPhotos()
//            binding.swipeRefreshLayout.isRefreshing = false
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}