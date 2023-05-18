package com.test.smartapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 0
    }

    fun connectToRemoteConfig(context: Context): LiveData<Boolean> {
        val appTypeLiveData = MutableLiveData<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            remoteConfig.setConfigSettingsAsync(configSettings).await()
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults).await()
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        appTypeLiveData.value = remoteConfig.getBoolean("appType")
                        Log.d(
                            "MainViewModel_34",
                            "Config params updated: ${remoteConfig.getBoolean("appType")}"
                        )
                        Toast.makeText(
                            context, "Fetch and activate succeeded",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context, "Fetch failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.await()
        }
        return appTypeLiveData
    }
}