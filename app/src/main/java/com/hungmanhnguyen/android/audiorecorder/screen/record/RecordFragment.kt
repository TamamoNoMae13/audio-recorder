package com.hungmanhnguyen.android.audiorecorder.screen.record

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
import androidx.navigation.Navigation
import com.hungmanhnguyen.android.audiorecorder.R
import com.hungmanhnguyen.android.audiorecorder.databinding.FragmentRecordBinding
import java.io.File
import java.io.IOException
import java.util.*

/** Define constants */
private const val REC_PERM = Manifest.permission.RECORD_AUDIO
private const val WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE
private const val READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE
private val PM_PERM_GRANTED = PackageManager.PERMISSION_GRANTED

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
//private const val OPUS = MediaRecorder.OutputFormat.OPUS

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        viewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

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
                    startRecording(viewModel.audioCodec.value!!,viewModel.channel.value!!,viewModel.sampleRate.value!!,viewModel.bitRate.value!!)
                } else Toast.makeText(context, "No permission", Toast.LENGTH_SHORT).show()
            }
        }

        // Settings Button
        binding.settingBtn.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_recordFragment_to_settingsFragment)
        }
//        binding.settingBtn.setOnClickListener {
//            if(viewModel.isRecording.value == true) {
//                announceIsRecord()
//            } else
//                Navigation.createNavigateOnClickListener(R.id.action_recordFragment_to_settingsFragment)
//        }
        // Record List Button
        binding.recordListBtn.setOnClickListener {
            if(viewModel.isRecording.value == true) {
                announceIsRecord()
            } else Navigation.createNavigateOnClickListener(R.id.action_recordFragment_to_recordListFragment)
        }

        return binding.root
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

    /** Announce when clicking other buttons while recording */
    private fun announceIsRecord() {
        Toast.makeText(context,"Stop recording first",Toast.LENGTH_SHORT).show()
    }

    /** Stop recording */
    private fun stopRecording() {

        // Stop the timer
        binding.recordTimer.stop()

        // Change the announcement text
        binding.announcement.text = getString(R.string.record_stop_announce, recFile)

        // MediaRecorder lifecycles
        mr!!.stop()
        mr!!.reset()
        mr!!.release()

        // Destroy old MediaRecorder
        mr = null
    }

    /** Start recording */
    private fun startRecording(codec: Int, channel: Int, sampleRate: Int, bitRate: Int) {
        // Params for recording preset
        var recCodec: Int
        var recFormat: Int
        var ext: String

        // Set preset from indices in radios
        when (codec) {
//            0 -> {
//                recCodec = PCM_16BIT
//                ext = ".wav"
//            }
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
//            4 -> {
//                recCodec = VORBIS
//                recFormat = WEBM
//                ext = ".webm"
//            }
//            5 -> {
//                recCodec = OPUS
//                recFormat = OGG
//                ext = ".ogg"
//            }
            else -> {
                recCodec = AMR_NB
                recFormat = THREE_GP
                ext = ".3gp"
            }
        }

        // Start timer from 0
        binding.recordTimer.base = SystemClock.elapsedRealtime()
        binding.recordTimer.start()

        // Get Date
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK)

        // Get app external path
        val recPath: String = requireActivity().getExternalFilesDir("/")!!.absolutePath

        // Formatting filename from date + extension
        recFile = "Record_" + formatter.format(Date()) + ext //".3gp" // ext

        // Change the announcement text
        binding.announcement.text = getString(R.string.record_start_announce, recFile)

        // Check if MediaRecorder is already there, in case destroyed via previous stopRecording()
        // init -> initialized
        if (mr == null) mr = MediaRecorder()

        // initialized -> source configured
        mr!!.setAudioSource(MediaRecorder.AudioSource.MIC)

        // keeps configuring sources
        mr!!.setOutputFormat(recFormat)
        mr!!.setAudioEncoder(recCodec)
        mr!!.setAudioChannels(channel)
        mr!!.setAudioSamplingRate(sampleRate)
        mr!!.setAudioEncodingBitRate(bitRate*1000)
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

        if (ActivityCompat.checkSelfPermission(requireContext(), REC_PERM) != PM_PERM_GRANTED
            ||
            ActivityCompat.checkSelfPermission(requireContext(), WRITE_PERM) != PM_PERM_GRANTED
        ) {
            requestPermissions(arrayOf(REC_PERM, WRITE_PERM), 101)
            return false
        }
        return true
    }

    /** Lifecycle Methods */
//    override fun onStart() {
//        super.onStart()
//    }

    override fun onStop() {
        super.onStop()
        if(viewModel.isRecording.value == true) stopRecording()
    }
}
