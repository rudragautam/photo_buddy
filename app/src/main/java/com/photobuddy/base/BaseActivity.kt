package com.photobuddy.base

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.util.Locale


abstract class BaseActivity : AppCompatActivity() {

    private var isConnected = true

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("LANGUAGE", "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread {
                if (!isConnected) {
                    isConnected = true
                    onInternetAvailable()
                }
            }
        }

        override fun onLost(network: Network) {
            runOnUiThread {
                if (isConnected) {
                    isConnected = false
                    onInternetLost()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Make the app full-screen and prevent the status bar and navigation bar from being shown
        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT*/
        /*val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = false*/
        registerNetworkCallback()

        // Register app background/foreground listener

        hideSystemUI()
        enableEdgeToEdge()

        // Handle back press logic
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (shouldHandleCustomBackPress()) {
                    // Handle custom logic here
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkCallback()
    }

    private fun isLockTaskModeActive(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
        } else {
            false
        }
    }


    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun unregisterNetworkCallback() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun onInternetLost() {
        // Handle loss of internet connection
        // Example: Start a NoInternetActivity
    }

    private fun onInternetAvailable() {
        // Handle restoration of internet connection
        // Example: Close the NoInternetActivity if it's open
    }

    protected fun showLoading() {
        try {
//            hideLoading()
//            showLoadingIndicator()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun hideLoading() {
        try {
//            hideLoadingIndicator()
//            mProgressDialog?.takeIf { it.isShowing }?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun isInternetAvailable(): Boolean {
//        val isAvailable = NetworkUtils.isNetworkAvailable(this)
//        if (!isAvailable) {
//            onInternetLost()
//        }
        return true
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.setPadding(
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    )
                }
            }
            insets
        }

        // Make the status bar transparent
//        window.statusBarColor = android.graphics.Color.TRANSPARENT
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
    }

    open fun onCustomBackPressed() {

    }

    open fun onBlockerPressed() {
        // Override in child classes for custom back press logic
    }

    open fun shouldHandleCustomBackPress(): Boolean {
        // Override in child classes to indicate custom handling
        return false
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    protected fun hideKeyboard() {
        val view = currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun handleBaseError(message: String?) {
        hideLoading()
    }

}
