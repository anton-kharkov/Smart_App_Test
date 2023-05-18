package com.test.smartapp

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.test.smartapp.ui.thame.SmartAppTestTheme

class WebActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = "https://www.google.com"

        setContent {
            SmartAppTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AndroidView(factory = {
                        WebView(it).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient()
                            settings.apply {
                                javaScriptEnabled = true
                                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                            }
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                            loadUrl(url)
                            webView = this
                        }
                    }, update = {
                        it.loadUrl(url)
                    })
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}