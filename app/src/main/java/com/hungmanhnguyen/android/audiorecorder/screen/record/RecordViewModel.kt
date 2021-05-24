package com.hungmanhnguyen.android.audiorecorder.screen.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {

    private val _permAllowed = MutableLiveData<Boolean>()
    val permAllowed: LiveData<Boolean>
        get() = _permAllowed

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean>
        get() = _isRecording

//    private val _audioCodec = MutableLiveData<String>()
//    val audioCodec: LiveData<String>
//        get() = _audioCodec

    init {
        Log.i("RecordVM", "RecordVM created")
        _permAllowed.value = false
        _isRecording.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("RecordVM", "RecordVM destroyed")
    }

    fun onPermAllowed() {
        _permAllowed.value = true
    }

    fun onRecordingStart() {
        _isRecording.value = true
    }

    fun onRecordingStop() {
        _isRecording.value = false
    }

//    fun setAudioCodec(codec: String) {
//        TODO("Not yet implemented.")
//    }
}
