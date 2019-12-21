package com.example.android.tj.activity.ui.songs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.tj.model.TJServiceSongMetadataList

class SongsViewModel : ViewModel() {

    val songsMetadataList: MutableLiveData<TJServiceSongMetadataList> by lazy {
        MutableLiveData<TJServiceSongMetadataList>()
    }
}