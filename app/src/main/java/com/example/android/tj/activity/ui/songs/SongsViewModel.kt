package com.example.android.tj.activity.ui.songs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.tj.model.TJServiceSongsSyncData

class SongsViewModel : ViewModel() {

    val songsSyncData: MutableLiveData<TJServiceSongsSyncData> by lazy {
        MutableLiveData<TJServiceSongsSyncData>()
    }
}