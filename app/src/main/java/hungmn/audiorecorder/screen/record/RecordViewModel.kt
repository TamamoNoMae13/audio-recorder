package hungmn.audiorecorder.screen.record

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

//    private val _isPcm = MutableLiveData<Boolean>()
//    val isPcm: LiveData<Boolean>
//        get() = _isPcm

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
		Log.i("RecordVM", "RecordVM created")
		_permAllowed.value = false
		_isRecording.value = false
		setAudioCodec(1)
		setChannel(2)
		setSampleRate(44100)
		setBitRate(128)
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

//    fun onEnablePcm() {
//        _isPcm.value = true
//    }
//
//    fun onDisablePcm() {
//        _isPcm.value = false
//    }

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
