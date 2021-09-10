package com.research.spotifyclone.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.research.spotifyclone.data.entities.Song
import com.research.spotifyclone.utils.Constants.COLLECTION_SONG
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val firestore= FirebaseFirestore.getInstance()
    private val songCollection= firestore.collection(COLLECTION_SONG)

    suspend fun getAllSongs(): List<Song>{
        return try {
            songCollection.get().await().toObjects(Song::class.java)

        }catch (e:Exception){
            emptyList()
        }
    }
}