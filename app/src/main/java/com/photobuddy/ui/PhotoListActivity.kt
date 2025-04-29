package com.photobuddy.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photobuddy.R
import com.photobuddy.base.BaseActivity
import com.photobuddy.data.model.Photo
import com.photobuddy.databinding.ActivityImageBinding
import com.photobuddy.ui.adapter.CarouselAdapter
import com.photobuddy.ui.adapter.PhotoListAdapter
import com.photobuddy.utils.dp
import com.photobuddy.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoListActivity : BaseActivity() {
    private lateinit var binding: ActivityImageBinding
    private val photoViewModel: PhotoViewModel by viewModels()
    private lateinit var carouselAdapter: PhotoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val PAGE=intent.getStringExtra("PAGE");
        photoViewModel.loadImageByFolderName(PAGE)
        observeViewModel()
        setupCloseButton()

    }


    private fun setupCarousel(photos: List<Photo>) {
        carouselAdapter = PhotoListAdapter(photos, Glide.with(this))
        binding.carouselRecyclerView.apply {
            layoutManager = GridLayoutManager(this@PhotoListActivity,5)
            adapter = carouselAdapter
//            setHasFixedSize(true)
//            addItemDecoration(CarouselItemDecoration())
        }



    }



    private fun setupCloseButton() {
        binding.closeButton.setOnClickListener {
            finish()
        }
    }
    private fun observeViewModel() {


        lifecycleScope.launch {
            photoViewModel.photos.collectLatest { photos ->
//                carouselAdapter.submitList(photos)
                setupCarousel(photos)
            }
        }

    }


}