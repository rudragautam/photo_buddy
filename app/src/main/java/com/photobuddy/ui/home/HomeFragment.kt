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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photobuddy.databinding.FragmentHomeBinding
import com.photobuddy.ui.adapter.PhotoAdapter
import com.photobuddy.utils.visible
import com.photobuddy.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val photoAdapter = PhotoAdapter()
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
        setupRefreshListener()
    }

    private fun setupRecyclerView() {
        binding.photoRecyclerView.apply {
            adapter = photoAdapter
            layoutManager=GridLayoutManager(requireActivity(),5)
            setHasFixedSize(true)
        }
        /*val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView( binding.photoRecyclerView)*/

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            photoViewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visible(isLoading)
                binding.photoRecyclerView.visible(!isLoading)
            }
        }

        lifecycleScope.launch {
            photoViewModel.photos.collectLatest { photos ->
                photoAdapter.submitList(photos)
                binding.emptyState.visible(photos.isEmpty())
            }
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

    private fun setupRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            photoViewModel.refreshPhotos()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}