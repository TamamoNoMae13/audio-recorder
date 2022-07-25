package hungmn.audiorecorder.screen.recordlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hungmn.audiorecorder.R
import hungmn.audiorecorder.screen.recordlist.RecordListAdapter.RecordListViewHolder
import java.io.File

class RecordListAdapter(
	private val recordFiles: Array<File>,
	private val onItemClick: OnItemClick
) : RecyclerView.Adapter<RecordListViewHolder>() {
	/** Declare related components */
	private var timeAgo: TimeAgo? = null

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): RecordListViewHolder {
		timeAgo = TimeAgo()
		return RecordListViewHolder(
			LayoutInflater.from(parent.context)
				.inflate(R.layout.single_list_item, parent, false)
		)
	}

	override fun onBindViewHolder(holder: RecordListViewHolder, position: Int) {
		holder.listTitle.text = recordFiles[position].name
		holder.listDate.text =
			timeAgo!!.getTimeAgo(recordFiles[position].lastModified())

//        val lastModified = Date(recordFiles[position].lastModified())
//        holder.listDate.text = DateFormat
//            .format("yyyy-MM-dd HH:mm", lastModified)
//            .toString()
	}

	override fun getItemCount(): Int {
		return recordFiles.size
	}

	inner class RecordListViewHolder(itemView: View) :
		RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
		/** Define Views */
//        var listImage: ImageView = itemView.findViewById(R.id.list_image_view)
		var listTitle: TextView = itemView.findViewById(R.id.list_title)
		var listDate: TextView = itemView.findViewById(R.id.list_date)

		override fun onClick(v: View) {
			onItemClick.onItemClick(
				recordFiles[adapterPosition], adapterPosition
			)
		}

		override fun onLongClick(v: View): Boolean {
			onItemClick.onItemLongClick(
				recordFiles[adapterPosition], adapterPosition
			)
			return true
		}

		init {
			itemView.setOnClickListener(this)
			itemView.setOnLongClickListener(this)
		}
	}

	interface OnItemClick : AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
		fun onItemClick(file: File, position: Int)
		fun onItemLongClick(file: File, position: Int)
	}
}
