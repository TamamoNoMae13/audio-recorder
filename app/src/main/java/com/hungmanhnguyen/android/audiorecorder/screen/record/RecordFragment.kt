package com.hungmanhnguyen.android.audiorecorder.screen.record

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.hungmanhnguyen.android.audiorecorder.R
import com.hungmanhnguyen.android.audiorecorder.databinding.FragmentRecordBinding
import java.util.Date
import java.util.Locale
import java.io.IOException

class RecordFragment : Fragment() {

    /** Mentioning related components */
    private lateinit var viewModel: RecordViewModel
    private lateinit var binding: FragmentRecordBinding

    /** Define variables */
    private var recFile: String? = null
    private var mr: MediaRecorder? = null

    /** Define constant */
    private val REC_PERM = Manifest.permission.RECORD_AUDIO
    private val WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE
//    private val READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE
    private val PM_PERM_GRANTED = PackageManager.PERMISSION_GRANTED

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
                    startRecording()
                } else Toast.makeText(requireContext(), "No permission", Toast.LENGTH_SHORT).show()
            }
        }

        // Settings Button
//        binding.settingBtn.setOnClickListener {
//
//        }

        // Record List Button
        binding.recordListBtn.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_recordFragment_to_recordListFragment)
        )

        return binding.root
    }

    /** Check perm after View generated */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkPerm()) viewModel.onPermAllowed()
    }

    /** Lifecycle methods */
    // Fix later, because app crash when navigating to other fragments
//    override fun onStop() {
//        super.onStop()
//
//        // MediaRecorder lifecycles
//        mr!!.stop()
//        mr!!.reset()
//        mr!!.release()
//
//        // Destroy old MediaRecorder
//        mr = null
//
//        viewModel.onRecordingStop()
//    }

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
        binding.announcement.text = getString(R.string.record_stop_announce, recFile)

        // MediaRecorder lifecycles
        mr!!.stop()
//        mr!!.reset()
        mr!!.release()

        // Destroy old MediaRecorder
        mr = null
    }

    /** Start recording */
    private fun startRecording() {

        // Start timer from 0
        binding.recordTimer.base = SystemClock.elapsedRealtime()
        binding.recordTimer.start()

        // Get Date
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK)

        // Get app external path
        val recPath: String = requireActivity().getExternalFilesDir("/")!!.absolutePath

        // Formatting filename from date + extension
        recFile = "Record_" + formatter.format(Date()) + ".3gp"

        // Change the announcement text
        binding.announcement.text = getString(R.string.record_start_announce, recFile)

        // Check if MediaRecorder is already there, in case destroyed via previous stopRecording()
        // init -> initialized
        if (mr == null) mr = MediaRecorder()

        // initialized -> source configured
        mr!!.setAudioSource(MediaRecorder.AudioSource.MIC)

        // keeps configuring sources
        mr!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mr!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
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
}
