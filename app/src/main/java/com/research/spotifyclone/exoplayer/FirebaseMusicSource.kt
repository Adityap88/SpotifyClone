package com.research.spotifyclone.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.research.spotifyclone.data.remote.MusicDatabase
import com.research.spotifyclone.exoplayer.State.*;
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {
    var songs = emptyList<MediaMetadataCompat>()
    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING
        val allSong = musicDatabase.getAllSongs()
        songs = allSong.map { song ->
            Builder()
                .putString(METADATA_KEY_ARTIST, song.subtitle)
                .putString(METADATA_KEY_MEDIA_ID, song.mediaId)
                .putString(METADATA_KEY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle)
                .build()
        }
        state = STATE_INITIALIZED


    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title.toString())
            .setSubtitle(song.description.subtitle.toString())
            .setIconUri(song.getString(METADATA_KEY_DISPLAY_ICON_URI).toUri())
            .setMediaId(song.description.mediaId.toString())
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach() { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    private val onReadyListener = mutableListOf<(Boolean) -> Unit>()
    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListener) {
                    field = value
                    onReadyListener.forEach() { listner ->
                        listner(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListener += action
            return false
        }
        action(state == STATE_INITIALIZED)
        return true
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR;

}