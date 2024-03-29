package com.hungmanhnguyen.android.audiorecorder.screen.recordlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordListViewModel : ViewModel() {

//    private val _permAllowed = MutableLiveData<Boolean>()
//    val permAllowed: LiveData<Boolean>
//        get() = _permAllowed

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    init {
        Log.i("RecordListVM", "RecordListVM created")
//        _permAllowed.value = false
        _isPlaying.value = false
    }

//    fun onPermAllowed() {
//        _permAllowed.value = true
//    }

    fun onPlayingStart() {
        _isPlaying.value = true
    }

    fun onPlayingStop() {
        _isPlaying.value = false
    }

    fun onPlayingPause() {
        _isPlaying.value = false
    }
}
