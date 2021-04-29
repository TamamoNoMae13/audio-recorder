package com.hungmanhnguyen.android.audiorecorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.hungmanhnguyen.android.audiorecorder.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        val binding: FragmentRecordBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_record, container, false)
//        binding.recordListBtn.setOnClickListener {view: View ->
//            view.findNavController().navigate(R.id.action_recordFragment_to_recordListFragment)
//        }
        binding.recordListBtn.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_recordFragment_to_recordListFragment)
        )
        return binding.root
    }
}