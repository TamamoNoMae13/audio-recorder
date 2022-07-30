package hungmn.audiorecorder.screen.recordlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import hungmn.audiorecorder.R
import hungmn.audiorecorder.dialog.ConfirmDialog
import hungmn.audiorecorder.dialog.RenameDialog
import hungmn.audiorecorder.screen.recordlist.RecordListAdapter.RecordListViewHolder
import java.io.File

class RecordListAdapter(
	private val activity: FragmentActivity,
	private val recordFiles: Array<File>,
	private val onItemListClick: OnItemListClick
) : RecyclerView.Adapter<RecordListViewHolder>() {
	/** Declare related components */
	private var timeAgo: TimeAgo? = null
	private var approvedDelete: Boolean? = null

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
		holder.deleteBtn.setOnClickListener {
//			approvedDelete = false
			val removedFile: File = recordFiles[position]
//			showConfirmDialog()
//			if (approvedDelete as Boolean) {
				val status = removedFile.delete()
				while (!status) continue
				recordFiles.drop(position)
				notifyDataSetChanged()
//			}
		}

		holder.renameBtn.setOnClickListener {
			val renamedFile: File = recordFiles[position]
			RenameDialog(renamedFile)
		}
	}

	private fun showConfirmDialog() {
		val dialog = ConfirmDialog()
		dialog.setTargetFragment(RecordListFragment(), 1)
		dialog.show(activity.supportFragmentManager, "dialog")
	}

	override fun getItemCount(): Int {
		return recordFiles.size
	}

	inner class RecordListViewHolder(itemView: View) :
		RecyclerView.ViewHolder(itemView), View.OnClickListener {
		/** Define Views */
		var listTitle: TextView = itemView.findViewById(R.id.list_title)
		var listDate: TextView = itemView.findViewById(R.id.list_date)
		var renameBtn: ImageView = itemView.findViewById(R.id.rename_btn)
		var convertBtn: ImageView = itemView.findViewById(R.id.convert_btn)
		var deleteBtn: ImageView = itemView.findViewById(R.id.delete_btn)

		override fun onClick(v: View) {
			onItemListClick.onItemListClick(
				recordFiles[adapterPosition], adapterPosition
			)
		}

		init {
			itemView.setOnClickListener(this)
		}
	}

	interface OnItemListClick : AdapterView.OnItemClickListener {
		fun onItemListClick(file: File, position: Int)
	}
}
