package com.hungmanhnguyen.android.audiorecorder.screen.recordlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hungmanhnguyen.android.audiorecorder.R

class RecordListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record_list, container, false)
    }
}