package hungmn.audiorecorder.screen.record

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import hungmn.audiorecorder.R
import hungmn.audiorecorder.databinding.FragmentRecordBinding

//import java.io.File
import java.io.IOException
import java.util.*

/** Define constants */
private const val REC_PERM = Manifest.permission.RECORD_AUDIO
private const val WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE

//private const val READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE
private const val PM_PERM_GRANTED = PackageManager.PERMISSION_GRANTED

/** Define formats */
//private const val WAV = "Not yet"
private const val M4A = MediaRecorder.OutputFormat.MPEG_4
private const val THREE_GP = MediaRecorder.OutputFormat.THREE_GPP
//private const val WEBM = MediaRecorder.OutputFormat.WEBM
//private const val OGG = MediaRecorder.OutputFormat.OGG

/** Define codecs */
//private const val WAV = "Not yet"
private const val AAC = MediaRecorder.AudioEncoder.AAC
private const val AMR_NB = MediaRecorder.AudioEncoder.AMR_NB
private const val AMR_WB = MediaRecorder.AudioEncoder.AMR_WB
//private const val VORBIS = MediaRecorder.AudioEncoder.VORBIS
//private const val OPUS = MediaRecorder.AudioEncoder.OPUS

class RecordFragment : Fragment() {

	/** Mentioning related components */
	private lateinit var viewModel: RecordViewModel
	private lateinit var binding: FragmentRecordBinding

	/** Define variables */
	private var recFile: String? = null
	private var mr: MediaRecorder? = null

	/** Setting up when init and create View */
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		/** View & Data Binding + declare ViewModel */
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_record,
			container,
			false
		)
		viewModel = ViewModelProvider(this)[RecordViewModel::class.java]

		/** Get SafeArgs bundle from SettingsFragment */
		val args = RecordFragmentArgs.fromBundle(requireArguments())
		viewModel.setAudioCodec(args.codec)
		viewModel.setChannel(args.channel)
		viewModel.setSampleRate(args.sampleRate)
		viewModel.setBitRate(args.bitRate)

		/** OnClick Handlers */
		// Record Button
		binding.recordBtn.setOnClickListener {
			if (viewModel.isRecording.value == true) {
				stopRecording()
				binding.recordBtn.setImageResource(R.drawable.ic_record_btn_start) // change to Start image
				viewModel.onRecordingStop()
			} else {
				if (viewModel.permAllowed.value == true) {
					viewModel.onRecordingStart()
					binding.recordBtn.setImageResource(R.drawable.ic_record_btn_stop) // change to Stop image
					startRecording(
						viewModel.audioCodec.value!!,
						viewModel.channel.value!!,
						viewModel.sampleRate.value!!,
						viewModel.bitRate.value!!
					)
				} else Toast.makeText(context, "No permission", Toast.LENGTH_SHORT)
					.show()
			}
		}

		// Settings Button
		binding.settingBtn.setOnClickListener { v: View ->
			if (viewModel.isRecording.value == true) announceIsRecord()
			else v.findNavController()
				.navigate(R.id.action_recordFragment_to_settingsFragment)
		}

		// Record List Button
		binding.recordListBtn.setOnClickListener { v: View ->
			if (viewModel.isRecording.value == true) announceIsRecord()
			else v.findNavController()
				.navigate(R.id.action_recordFragment_to_recordListFragment)
		}

		return binding.root
	}

	/** Announce when isRecording */
	private fun announceIsRecord() {
		Toast.makeText(
			requireContext(),
			"Stop recording first!",
			Toast.LENGTH_SHORT
		).show()
	}

	/** Check perm after View generated */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (checkPerm()) viewModel.onPermAllowed()
	}

	/** Change boolean to true after perm granted */
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		// super.registerForActivityResult()
		if (requestCode == 101 && grantResults[0] == PM_PERM_GRANTED)
			viewModel.onPermAllowed()
	}

	/** Stop recording */
	private fun stopRecording() {

		// Stop the timer
		binding.recordTimer.stop()

		// Change the announcement text
		binding.announcement.text =
			getString(R.string.record_stop_announce, recFile)

		// MediaRecorder lifecycles
		mr!!.stop()
		mr!!.reset()
		mr!!.release()

		// Destroy old MediaRecorder
		mr = null
	}

	/** Start recording */
	private fun startRecording(
		codec: Int,
		channel: Int,
		sampleRate: Int,
		bitRate: Int
	) {

		// Params for recording preset
		val recCodec: Int
		val recFormat: Int
		val ext: String

		// Set preset from indices in radios
		when (codec) {
//			0 -> {
//				recCodec = PCM_16BIT
//				ext = ".wav"
//			}
			1 -> {
				recCodec = AAC
				recFormat = M4A
				ext = ".m4a"
			}
			2 -> {
				recCodec = AMR_NB
				recFormat = THREE_GP
				ext = ".3gp"
			}
			3 -> {
				recCodec = AMR_WB
				recFormat = THREE_GP
				ext = ".3gp"
			}
//			4 -> {
//				recCodec = VORBIS
//				recFormat = WEBM
//				ext = ".webm"
//			}
//			5 -> {
//				recCodec = OPUS
//				recFormat = OGG
//				ext = ".ogg"
//			}
			else -> {
				recCodec = AAC
				recFormat = M4A
				ext = ".m4a"
			}
		}

		val sample: Int = if (sampleRate > 48000) 48000
		else sampleRate

		// Start timer from 0
		binding.recordTimer.base = SystemClock.elapsedRealtime()
		binding.recordTimer.start()

		// Get Date
		val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK)

		// Get app external path
		val recPath: String =
			requireActivity().getExternalFilesDir("/")!!.absolutePath

		// Formatting filename from date + extension
		recFile = "Record_" + formatter.format(Date()) + ext

		// Change the announcement text
		binding.announcement.text =
			getString(R.string.record_start_announce, recFile)

		// Check if MediaRecorder is already there, in case destroyed via previous stopRecording()
		// init -> initialized
		if (mr == null) mr = MediaRecorder()

		// initialized -> source configured
		mr!!.setAudioSource(MediaRecorder.AudioSource.MIC)

		// keeps configuring sources
		mr!!.setOutputFormat(recFormat)
		mr!!.setAudioEncoder(recCodec)

		if (recFormat != THREE_GP) {
			mr!!.setAudioChannels(channel)
			mr!!.setAudioSamplingRate(sample)
			mr!!.setAudioEncodingBitRate(bitRate * 1000)
		}
		mr!!.setOutputFile("$recPath/$recFile")

		// Prepare, then start if no error occurs, MediaRecorder Lifecycles
		try {
			mr!!.prepare()
		} catch (e: IOException) {
			Log.e("MediaRecorder", "prepare() failed")
			e.printStackTrace()
		}

		// Start recording
		mr!!.start()
	}

	/** Checking Record and Write permissions */
	private fun checkPerm(): Boolean {

		if (ActivityCompat.checkSelfPermission(
				requireContext(),
				REC_PERM
			) != PM_PERM_GRANTED
			||
			ActivityCompat.checkSelfPermission(
				requireContext(),
				WRITE_PERM
			) != PM_PERM_GRANTED
		) {
			requestPermissions(arrayOf(REC_PERM, WRITE_PERM), 101)
			return false
		}
		return true
	}

	/** Lifecycle Methods */

	override fun onStop() {
		super.onStop()
		if (viewModel.isRecording.value == true) stopRecording()
	}
}
