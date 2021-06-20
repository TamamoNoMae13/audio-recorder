package com.hungmanhnguyen.android.audiorecorder.screen.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    private val _audioCodec = MutableLiveData<Int>()
    val audioCodec: LiveData<Int>
        get() = _audioCodec

    private val _channel = MutableLiveData<Int>()
    val channel: LiveData<Int>
        get() = _channel

    private val _sampleRate = MutableLiveData<Int>()
    val sampleRate: LiveData<Int>
        get() = _sampleRate

    private val _bitRate = MutableLiveData<Int>()
    val bitRate: LiveData<Int>
        get() = _bitRate

    init {
        Log.i("SettingVM", "SettingVM created")
        setAudioCodec(1)
        setChannel(2)
        setSampleRate(44100)
        setBitRate(128)
    }

    fun setAudioCodec(codec: Int) {
        _audioCodec.value = codec
    }

    fun setChannel(channel: Int) {
        _channel.value = channel
    }

    fun setSampleRate(sampleRate: Int) {
        _sampleRate.value = sampleRate
    }

    fun setBitRate(bitRate: Int) {
        _bitRate.value = bitRate
    }
}