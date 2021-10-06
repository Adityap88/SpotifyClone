package com.research.spotifyclone

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpotifyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
//        FirebaseApp.initializeApp(applicationContext)
    }

}