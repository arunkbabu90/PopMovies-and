package arunkbabu90.popmovies.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.databinding.ActivityViewPictureBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlin.math.max
import kotlin.math.min

class ViewPictureActivity : AppCompatActivity(), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private lateinit var binding: ActivityViewPictureBinding
    private lateinit var scaleDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetectorCompat

    private var activePointerId: Int = MotionEvent.INVALID_POINTER_ID
    private var scaleFactor: Float = 1.0f
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var posX: Float = 0f
    private var posY: Float = 0f

    companion object {
        const val PROFILE_PICTURE_PATH_EXTRA_KEY = "key_profile_picture_path_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        // Status bar and Navigation bar colors
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)

        val imagePath = intent.getStringExtra(PROFILE_PICTURE_PATH_EXTRA_KEY)

        Glide.with(this).load(imagePath)
            .error(R.drawable.default_dp)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadStarted(placeholder: Drawable?) {
                    binding.pbViewPicture.visibility = View.VISIBLE
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.ivViewPicture.setImageDrawable(resource)
                    binding.pbViewPicture.visibility = View.GONE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.ivViewPicture.setImageDrawable(null)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    binding.ivViewPicture.setImageDrawable(errorDrawable)
                    binding.pbViewPicture.visibility = View.GONE
                }
            })

        gestureDetector = GestureDetectorCompat(this, this)
        gestureDetector.setOnDoubleTapListener(this)
        scaleDetector = ScaleGestureDetector(this, ScaleGestureListener())
    }


    /**
     * Resets the ImageView to its original size or scales it up
     * @param event The MotionEvent object
     */
    private fun resetOrZoom(event: MotionEvent?) {
        if (scaleFactor > 1.0f || scaleFactor < 1.0f) {
            // Reset
            lastTouchX = 0f
            lastTouchY = 0f
            posX = 0f
            posY = 0f
            activePointerId = MotionEvent.INVALID_POINTER_ID
            scaleFactor = 1.0f
        } else {
            // Zoom
            lastTouchX = 0f
            lastTouchY = 0f
            posX = 0f
            posY = 0f
            activePointerId = event?.getPointerId(0) ?: -1
            scaleFactor = 2.0f
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
        }
        invalidate()
    }

    /**
     * Applies the current position and scale to the ImageView
     */
    private fun invalidate() {
        binding.ivViewPicture.scaleX = scaleFactor
        binding.ivViewPicture.scaleY = scaleFactor
        binding.ivViewPicture.x = posX
        binding.ivViewPicture.y = posY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event?.action ?: -1 and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = event?.actionIndex ?: 0
                // Remember where we started dragging
                lastTouchX = event?.getX(pointerIndex) ?: 0f
                lastTouchY = event?.getY(pointerIndex) ?: 0f
                // Save the ID of the pointer for dragging
                activePointerId = event?.getPointerId(0) ?: -1
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event?.findPointerIndex(activePointerId) ?: 0
                val x = event?.getX(pointerIndex) ?: 0f
                val y = event?.getY(pointerIndex) ?: 0f

                // Calculate the distance moved
                val dx = x - lastTouchX
                val dy = y - lastTouchY

                posX += dx
                posY += dy

                invalidate()

                // Cache touch position for next zoom so that when zooming again ,it
                // won't reset its position
                lastTouchX = x
                lastTouchY = y
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event?.actionIndex ?: 0
                val pointerId = event?.getPointerId(pointerIndex) ?: 0

                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event?.getX(newPointerIndex) ?: 0f
                    lastTouchY = event?.getY(newPointerIndex) ?: 0f
                    activePointerId = event?.getPointerId(newPointerIndex) ?: -1
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
            }
        }
        return true
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures
     */
    private inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector?.scaleFactor ?: 1.0f
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            binding.ivViewPicture.scaleX = scaleFactor
            binding.ivViewPicture.scaleY = scaleFactor
            return true
        }
    }

    override fun onDown(p0: MotionEvent?): Boolean = false
    override fun onShowPress(p0: MotionEvent?) {}
    override fun onLongPress(p0: MotionEvent?) { }
    override fun onSingleTapUp(p0: MotionEvent?): Boolean = false
    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = false
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = false
    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean = false
    override fun onDoubleTapEvent(p0: MotionEvent?): Boolean = false
    override fun onDoubleTap(p0: MotionEvent?): Boolean {
        resetOrZoom(p0)
        return true
    }
}