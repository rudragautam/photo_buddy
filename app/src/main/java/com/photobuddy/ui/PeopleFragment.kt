package com.photobuddy.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.photobuddy.R
import com.photobuddy.data.model.FaceGroup
import com.photobuddy.databinding.FragmentPeopleBinding
import com.photobuddy.ui.adapter.PeopleAdapter
import com.photobuddy.utils.tflitehelper.FaceGroupManager

class PeopleFragment : Fragment() {

    private lateinit var binding: FragmentPeopleBinding
    private lateinit var adapter: PeopleAdapter
    private lateinit var faceGroupManager: FaceGroupManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        faceGroupManager = FaceGroupManager(requireActivity()) // Assume already populated
        val faceGroups = faceGroupManager.getFaceGroupsAsUiModel()


        adapter = PeopleAdapter(faceGroups) { faceGroup ->
            val fragment = faceGroup.imagePaths?.let { PersonGalleryFragment.newInstance(it) }
            fragment?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, it)
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }
}
