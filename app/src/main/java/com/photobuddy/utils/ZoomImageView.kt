package com.photobuddy.utils

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    private val matrix = Matrix()
    private val matrixValues = FloatArray(9)

    private var mode = NONE
    private val last = PointF()
    private val start = PointF()

    private var minScale = 1f
    private var maxScale = 4f
    private var saveScale = 1f

    private var origWidth = 0f
    private var origHeight = 0f
    private var viewWidth = 0
    private var viewHeight = 0

    private val scaleDetector: ScaleGestureDetector

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = matrix
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        setOnTouchListener { _, event -> onTouch(event) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val newWidth = MeasureSpec.getSize(widthMeasureSpec)
        val newHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (viewWidth != newWidth || viewHeight != newHeight) {
            viewWidth = newWidth
            viewHeight = newHeight
            fitImageToView()
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (viewWidth > 0 && viewHeight > 0) {
            fitImageToView()
        }
    }

    private fun fitImageToView() {
        val drawable = drawable ?: return

        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight

        val scaleX = viewWidth.toFloat() / imageWidth
        val scaleY = viewHeight.toFloat() / imageHeight
        val scale = min(scaleX, scaleY)

        matrix.setScale(scale, scale)

        val redundantXSpace = viewWidth - scale * imageWidth
        val redundantYSpace = viewHeight - scale * imageHeight
        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2)

        imageMatrix = matrix

        saveScale = 1f
        minScale = scale
        origWidth = viewWidth - redundantXSpace
        origHeight = viewHeight - redundantYSpace
    }

    private fun onTouch(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        val curr = PointF(event.x, event.y)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                last.set(curr)
                start.set(last)
                mode = DRAG
            }

            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    val dx = curr.x - last.x
                    val dy = curr.y - last.y

                    matrix.getValues(matrixValues)
                    val fixTransX = getFixDragTrans(dx, viewWidth.toFloat(), origWidth * saveScale)
                    val fixTransY = getFixDragTrans(dy, viewHeight.toFloat(), origHeight * saveScale)
                    matrix.postTranslate(fixTransX, fixTransY)
                    fixTrans()
                    imageMatrix = matrix
                    last.set(curr.x, curr.y)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        return true
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) 0f else delta
    }

    private fun fixTrans() {
        matrix.getValues(matrixValues)
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), origWidth * saveScale)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), origHeight * saveScale)

        if (fixTransX != 0f || fixTransY != 0f) {
            matrix.postTranslate(fixTransX, fixTransY)
        }
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans = viewSize - contentSize
        return when {
            contentSize <= viewSize -> 0f
            trans < minTrans -> -trans + minTrans
            trans > 0f -> -trans
            else -> 0f
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = detector.scaleFactor
            val origScale = saveScale
            saveScale *= scaleFactor

            saveScale = max(minScale, min(saveScale, maxScale))
            scaleFactor = saveScale / origScale

            val focusX = detector.focusX
            val focusY = detector.focusY

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
                matrix.postScale(scaleFactor, scaleFactor, viewWidth / 2f, viewHeight / 2f)
            } else {
                matrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            }

            fixTrans()
            imageMatrix = matrix
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
}
