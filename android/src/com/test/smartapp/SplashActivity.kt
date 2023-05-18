package com.test.smartapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import com.test.smartapp.ui.components.SplashScreen
import com.test.smartapp.ui.thame.SmartAppTestTheme
import dagger.hilt.android.AndroidEntryPoint

    private const val PREF_KEY = "app_pref"
    private const val APP_TYPE_KEY = "app_type_key"
    private const val FIRST_RUN = "first_run"

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences

    private var appType = false
    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

        firstRun = sharedPreferences.getBoolean(FIRST_RUN, true)
        appType = sharedPreferences.getBoolean(APP_TYPE_KEY, false)

        setContent {
            SmartAppTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SplashScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firstRun) {
            if (!isNetworkAvailable()) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle(R.string.no_internet_connection)
                    .setMessage(R.string.message_requires_internet_connection)
                    .setCancelable(false)
                    .setNegativeButton(R.string.close_app) { _, _ ->
                        finish()
                    }
                    .create()
                dialog.show()
            } else {
                splashViewModel.connectToRemoteConfig(this).observe(this) {
                    appType = it
                    openAppType()
                    sharedPreferences.edit {
                        putBoolean(FIRST_RUN, false)
                        putBoolean(APP_TYPE_KEY, it)
                    }
                }
            }
        } else {
            openAppType()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
    }

    private fun openAppType() {
        if (appType) {
            val intent = Intent(this, WebActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val intent = Intent(this, AndroidLauncher::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}