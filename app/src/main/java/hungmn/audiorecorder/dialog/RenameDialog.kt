package hungmn.audiorecorder.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import hungmn.audiorecorder.R
import java.io.File

class RenameDialog(private val oldFile: File): AppCompatDialogFragment() {
	private lateinit var renameText: EditText

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val builder = AlertDialog.Builder(requireActivity())

		val inflater = requireActivity().layoutInflater
		val view = inflater.inflate(R.layout.dialog_rename, null)

		renameText = view.findViewById(R.id.filename_edit)
		renameText.setText(oldFile.name)

		builder.setView(view)
			.setTitle(getString(R.string.rename))
			.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
			.setPositiveButton("OK") { _, _ ->
				val newFileName = renameText.text.toString()
				changeFileName(newFileName)
			}

		return builder.create()
	}

	private fun changeFileName(newFileName: String) {
		val dir = oldFile.absolutePath
		val newFile = File(dir, newFileName)
		if (oldFile.exists()) oldFile.renameTo(newFile)
	}
}