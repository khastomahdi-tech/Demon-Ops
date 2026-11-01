package com.demon.ops

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

/**
 * DEMON OPS — بازی تیراندازی سه‌بعدی آفلاین
 * موتور بازی: Three.js داخل WebView (assets/game) — بدون نیاز به اینترنت
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private lateinit var splash: View
    private var lastBack = 0L

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        web = findViewById(R.id.web)
        splash = findViewById(R.id.splash)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        web.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        web.setBackgroundColor(0xFF05060A.toInt())
        web.addJavascriptInterface(Bridge(), "DEMON")
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        web.loadUrl("file:///android_asset/game/index.html")
    }

    inner class Bridge {
        @JavascriptInterface
        fun vibrate(ms: Int) {
            try {
                val v: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
                    (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
                } else {
                    @Suppress("DEPRECATION") getSystemService(VIBRATOR_SERVICE) as Vibrator
                }
                v.vibrate(VibrationEffect.createOneShot(ms.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
            } catch (_: Exception) {}
        }

        @JavascriptInterface
        fun ready() {
            runOnUiThread { splash.visibility = View.GONE }
        }

        @JavascriptInterface
        fun exit() {
            runOnUiThread { finish() }
        }
    }

    override fun onBackPressed() {
        // دو بار بک = خروج (موقع بازی بک نباید وسط کار بزنه)
        val now = System.currentTimeMillis()
        if (now - lastBack < 2200) {
            finish()
        } else {
            lastBack = now
            Toast.makeText(this, "برای خروج دوباره بک", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    override fun onDestroy() {
        try {
            web.loadUrl("about:blank")
            web.destroy()
        } catch (_: Exception) {}
        super.onDestroy()
    }
}
