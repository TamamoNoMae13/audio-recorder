package com.hungmanhnguyen.android.audiorecorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this,R.layout.activity_main)
//        val navController = this.findNavController(R.id.fragment_container)
        setContentView(R.layout.activity_main)
    }

}