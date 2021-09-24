package com.research.spotifyclone.exoplayer.callbacks

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.research.spotifyclone.exoplayer.MusicService

class NotificationListener(
    private val musicService: MusicService
) :PlayerNotificationManager.NotificationListener{

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            stopForeground(true)
            isForeground=false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if(ongoing && !isForeground){
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext,this::class.java)
                )
                startForeground(1,notification)
                isForeground=true
            }
        }
    }
}