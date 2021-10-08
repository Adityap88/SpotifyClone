package com.research.spotifyclone.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.research.spotifyclone.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.title.toString() ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}