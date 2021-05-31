package com.hungmanhnguyen.android.audiorecorder.screen.recordlist

import com.hungmanhnguyen.android.audiorecorder.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hungmanhnguyen.android.audiorecorder.screen.recordlist.RecordListAdapter.RecordListViewHolder
import java.io.File

class RecordListAdapter(private var recordFiles: Array<File>): RecyclerView.Adapter<RecordListViewHolder>() {
    /** Declare related components */
    //private var timeAgo: TimeAgo? = null

    inner class RecordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /** Define variables */
        var listImage: ImageView = itemView.findViewById(R.id.list_image_view)
        var listTitle: TextView = itemView.findViewById(R.id.list_title)
        var listDate: TextView = itemView.findViewById(R.id.list_date)

//        override fun onClick(v: View) {
//            OnItemListClick.setOnClickListener(recordFiles!![adapterPosition], adapterPosition)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordListViewHolder {
        //timeAgo = TimeAgo()
        return RecordListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecordListViewHolder, position: Int) {
        holder.listTitle.text = recordFiles!![position].name
        holder.listDate.text = "abc"
        //holder.listDate.text = timeAgo!!.getTimeAgo(recordFiles!![position].lastModified())
    }

    override fun getItemCount(): Int {
        return recordFiles!!.size
    }

//    interface OnItemListClick {
//        fun setOnClickListener(file: File?, position: Int)
//    }
}