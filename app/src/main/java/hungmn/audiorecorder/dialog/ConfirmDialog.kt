package hungmn.audiorecorder.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import hungmn.audiorecorder.R

class ConfirmDialog: AppCompatDialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val builder = AlertDialog.Builder(requireActivity())
		val inflater = requireActivity().layoutInflater

		builder.setTitle(getString(R.string.confirm_title))
			.setNegativeButton(R.string.cancel) { _, _ -> }
			.setPositiveButton("OK") { _, _ ->

			}

		return builder.create()
	}
}