package com.photobuddy.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photobuddy.R
import com.photobuddy.base.BaseActivity
import com.photobuddy.data.model.Photo
import com.photobuddy.databinding.ActivityFullScreenImageBinding
import com.photobuddy.ui.adapter.CarouselAdapter
import com.photobuddy.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FullScreenImageActivity : BaseActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var carouselAdapter: CarouselAdapter
    private val photoViewModel: PhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        photoViewModel.refreshPhotos()
        observeViewModel()

        setupCloseButton()
    }
    private fun setupCarousel(photos: List<Photo>) {
        carouselAdapter = CarouselAdapter(photos, Glide.with(this))
        binding.carouselRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FullScreenImageActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = carouselAdapter
            setHasFixedSize(true)
            addItemDecoration(CarouselItemDecoration())
        }

        // Snap to center behavior
      /*  val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView)*/

        // Center item listener
        binding.carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateIndicators()
                }
            }
        })

        setupIndicators(photos)
    }

    private fun setupIndicators(photos: List<Photo>) {
        binding.indicatorsLayout.removeAllViews()
        for (i in photos.indices) {
            val indicator = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(16.dp, 16.dp).apply {
                    setMargins(4.dp, 0, 4.dp, 0)
                }
                setImageResource(if (i == 0) R.drawable.indicator_selected else R.drawable.indicator_unselected)
            }
            binding.indicatorsLayout.addView(indicator)
        }
    }

    private fun updateIndicators() {
        val layoutManager = binding.carouselRecyclerView.layoutManager as LinearLayoutManager
        val centerPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

        for (i in 0 until binding.indicatorsLayout.childCount) {
            val indicator = binding.indicatorsLayout.getChildAt(i) as ImageView
            indicator.setImageResource(
                if (i == centerPosition) R.drawable.indicator_selected
                else R.drawable.indicator_unselected
            )
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

    // DP conversion extension
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    class CarouselItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val isFirst = position == 0
            val isLast = position == parent.adapter?.itemCount?.minus(1)

            val horizontalMargin = 16 // Convert to pixels
            outRect.left = if (isFirst) horizontalMargin else horizontalMargin / 2
            outRect.right = if (isLast) horizontalMargin else horizontalMargin / 2
        }
    }
}

