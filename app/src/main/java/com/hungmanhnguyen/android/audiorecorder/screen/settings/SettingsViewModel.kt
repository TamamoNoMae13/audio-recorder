package com.hungmanhnguyen.android.audiorecorder.screen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

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

    fun saveConfigToFile(configFile: File) {
        configFile.bufferedWriter().use { out ->
            out.write(audioCodec.value!!)
            out.write("\n")
            out.write(channel.value!!)
            out.write("\n")
            out.write(sampleRate.value!!)
            out.write("\n")
            out.write(bitRate.value!!)
        }
    }
}