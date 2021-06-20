package com.hungmanhnguyen.android.audiorecorder.screen.recordlist

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hungmanhnguyen.android.audiorecorder.R
import com.hungmanhnguyen.android.audiorecorder.databinding.FragmentRecordListBinding
import java.io.File
import java.io.IOException

/** Define constant */
//private const val READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE
//private const val PM_PERM_GRANTED = PackageManager.PERMISSION_GRANTED

class RecordListFragment : Fragment(), RecordListAdapter.OnItemListClick {

    /** Mentioning related components */
    private lateinit var binding: FragmentRecordListBinding
    private lateinit var viewModel: RecordListViewModel

    /** Define related components as variables */
    private lateinit var recordListAdapter: RecordListAdapter
    private var recordFiles: Array<File>? = null
    private var mp: MediaPlayer? = null
    private var fileToPlay: File? = null
    private var seekbarHandler: Handler? = null
    private var updateSeekbar: Runnable? = null

    /** Setting up when init and create View */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /** View & Data Binding + declare ViewModel */
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_list, container, false)
        viewModel = ViewModelProvider(this).get(RecordListViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path: String = requireActivity().getExternalFilesDir("/")!!.absolutePath
        val directory = File(path)
        recordFiles = directory.listFiles()

        recordListAdapter = RecordListAdapter(recordFiles!!, this)
        binding.recordListFrag.setHasFixedSize(true)
        binding.recordListFrag.layoutManager = LinearLayoutManager(context)
        binding.recordListFrag.adapter = recordListAdapter

        /** OnClick Handlers */
        // Play Button
        binding.playButton.setOnClickListener {
            if (viewModel.isPlaying.value == true) pauseAudio()
            else if (fileToPlay != null) resumeAudio()
        }
        // Rewind Button
        binding.rewButton.setOnClickListener {}
        // Forward Button
        binding.fwdButton.setOnClickListener {}
        // Seekbar
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                pauseAudio()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                mp!!.seekTo(progress)
                resumeAudio()
            }
        })
    }

    /** When clicking one of the records */
    override fun onItemListClick(file: File, position: Int) {
        fileToPlay = file
        if (viewModel.isPlaying.value == true) {
            stopAudio()
            playAudio(fileToPlay)
        } else {
            playAudio(fileToPlay)
        }
    }

    /** Play/Pause/Resume/Stop audio func */
    private fun playAudio(fileToPlay: File?) {
        mp = MediaPlayer()
        try {
            mp!!.setDataSource(fileToPlay!!.absolutePath)
            mp!!.prepare()
            mp!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.playButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_pause_button,
                null
            )
        )
        binding.filename.text = fileToPlay!!.name
        binding.headerStatus.text = getString(R.string.playing)
        viewModel.onPlayingStart()
        mp!!.setOnCompletionListener {
            stopAudio()
            binding.headerStatus.text = getString(R.string.finished)
        }
        binding.seekbar.max = mp!!.duration
        seekbarHandler = Handler()
        updateRunnable()
        seekbarHandler!!.postDelayed(updateSeekbar!!, 0)
    }

    private fun pauseAudio() {
        mp!!.pause()
        binding.playButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_play_button,
                null
            )
        )
        viewModel.onPlayingPause()
        seekbarHandler!!.removeCallbacks(updateSeekbar!!)
    }

    private fun resumeAudio() {
        mp!!.start()
        binding.playButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_pause_button,
                null
            )
        )
        viewModel.onPlayingStart()
        updateRunnable()
        seekbarHandler!!.postDelayed(updateSeekbar!!, 0)
    }

    private fun stopAudio() {
        mp!!.stop()
        binding.playButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_play_button,
                null
            )
        )
        viewModel.onPlayingStop()
        binding.headerStatus.text = getString(R.string.stopped)
        seekbarHandler!!.removeCallbacks(updateSeekbar!!)
    }

    /** Seekbar update */
    private fun updateRunnable() {
        updateSeekbar = object : Runnable {
            override fun run() {
                binding.seekbar.progress = mp!!.currentPosition
                seekbarHandler!!.postDelayed(this, 500)
            }
        }
    }

    /** Leave here so this class can implement interface/abstract classes */
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}

    /** Lifecycle methods */
    override fun onStop() {
        super.onStop()
        if (viewModel.isPlaying.value == true) stopAudio()
    }
}