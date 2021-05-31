package com.hungmanhnguyen.android.audiorecorder.screen.recordlist

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hungmanhnguyen.android.audiorecorder.R
import com.hungmanhnguyen.android.audiorecorder.databinding.FragmentRecordListBinding
import java.io.File

class RecordListFragment : Fragment() {

    /** Mentioning related components */
    private lateinit var binding: FragmentRecordListBinding
    private lateinit var viewModel: RecordListViewModel

    /** Define related components as variables */
    // private var mp: MediaPlayer? = null
    private var playerSheet: ConstraintLayout = requireView().findViewById(R.id.player_sheet)
    private var bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(playerSheet)
    private var recordList: RecyclerView = requireView().findViewById(R.id.record_list_frag)
    private var recordFiles: Array<File>? = null
    private var recordListAdapter: RecordListAdapter? = null

    /** Define constant */
    private val READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE
    private val PM_PERM_GRANTED = PackageManager.PERMISSION_GRANTED

    /** Setting up when init and create View */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /** View & Data Binding + declare ViewModel */
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_list, container, false)
        viewModel = ViewModelProvider(this).get(RecordListViewModel::class.java)

        val path: String = requireActivity().getExternalFilesDir("/")!!.absolutePath
        val directory = File(path)
        recordFiles = directory.listFiles()

        recordListAdapter = RecordListAdapter(recordFiles!!)
        recordList.setHasFixedSize(true)
        recordList.layoutManager = LinearLayoutManager(context)
        recordList.adapter = recordListAdapter


        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Empty, just sliding, no effect after sliding.
            }
        })
        return binding.root
    }
}