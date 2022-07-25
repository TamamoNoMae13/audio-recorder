package hungmn.audiorecorder.screen.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hungmn.audiorecorder.Constant

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

	private val _audioCodec = MutableLiveData<Constant.Codec>()
	val audioCodec: LiveData<Constant.Codec>
		get() = _audioCodec

	private val _channel = MutableLiveData<Constant.Channel>()
	val channel: LiveData<Constant.Channel>
		get() = _channel

	private val _sampleRate = MutableLiveData<Constant.SampleRate>()
	val sampleRate: LiveData<Constant.SampleRate>
		get() = _sampleRate

	private val _bitRate = MutableLiveData<Constant.KBitRate>()
	val bitRate: LiveData<Constant.KBitRate>
		get() = _bitRate

	init {
		Log.i("RecordVM", "RecordVM created")
		_permAllowed.value = false
		_isRecording.value = false
		setAudioCodec(Constant.Codec.AAC)
		setChannel(Constant.Channel.STEREO)
		setSampleRate(Constant.SampleRate.FORTY_FOUR_THOUSAND)
		setBitRate(Constant.KBitRate.ONE_HUNDRED_TWENTY_EIGHT)
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

	fun setAudioCodec(codec: Constant.Codec) {
		_audioCodec.value = codec
	}

	fun setChannel(channel: Constant.Channel) {
		_channel.value = channel
	}

	fun setSampleRate(sampleRate: Constant.SampleRate) {
		_sampleRate.value = sampleRate
	}

	fun setBitRate(bitRate: Constant.KBitRate) {
		_bitRate.value = bitRate
	}
}
