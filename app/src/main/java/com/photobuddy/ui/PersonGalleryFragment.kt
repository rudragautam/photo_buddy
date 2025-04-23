package com.photobuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.photobuddy.databinding.FragmentPersonGalleryBinding
import com.photobuddy.ui.adapter.ImageGridAdapter

class PersonGalleryFragment : Fragment() {

    private lateinit var binding: FragmentPersonGalleryBinding
    private lateinit var imagePaths: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePaths = arguments?.getStringArrayList(ARG_IMAGE_PATHS) ?: emptyList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPersonGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ImageGridAdapter(imagePaths)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = adapter
    }

    companion object {
        private const val ARG_IMAGE_PATHS = "image_paths"
        fun newInstance(paths: List<String>) = PersonGalleryFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList(ARG_IMAGE_PATHS, ArrayList(paths))
            }
        }
    }
}
