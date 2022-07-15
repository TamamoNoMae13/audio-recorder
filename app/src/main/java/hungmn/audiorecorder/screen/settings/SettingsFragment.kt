package hungmn.audiorecorder.screen.settings

//import android.widget.Toast
//import java.io.File
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import hungmn.audiorecorder.R
import hungmn.audiorecorder.databinding.FragmentSettingsBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class SettingsFragment : Fragment() {

	/** Mentioning related components */
	private lateinit var viewModel: SettingsViewModel
	private lateinit var binding: FragmentSettingsBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		/** View & Data Binding + declare ViewModel */
		binding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_settings, container, false
		)
		viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		updateConfigOnSelected()
		updateDesc()

		/** Radio OnClick Handlers */
		binding.codecRadio.setOnCheckedChangeListener { _, _ ->
			codecOnClicked()
			updateConfigOnCodecSelected()
			updateDesc()
		}

		binding.channelRadio.setOnCheckedChangeListener { _, _ ->
			updateConfigOnChannelSelected()
			updateDesc()
		}

		binding.sampleRateRadio.setOnCheckedChangeListener { _, _ ->
			updateConfigOnSampleRateSelected()
			updateDesc()
		}

		binding.bitrateRadio.setOnCheckedChangeListener { _, _ ->
			updateConfigOnBitRateSelected()
			updateDesc()
		}

		binding.saveSettingBtn.setOnClickListener { v: View ->
			updateConfigOnSelected()
//			saveCfgToFile()
			val a: Int = viewModel.audioCodec.value!!
			val b: Int = viewModel.channel.value!!
			val c: Int = viewModel.sampleRate.value!!
			val d: Int = viewModel.bitRate.value!!
			v.findNavController().navigate(
				SettingsFragmentDirections.actionSettingsFragmentToRecordFragment(
					a, b, c, d
				)
			)
			Toast.makeText(
				requireContext(), "Settings saved!", Toast.LENGTH_LONG
			).show()
		}
	}

//	private fun saveCfgToFile() {
//		val savePath: String =
//			requireActivity().getExternalFilesDir("/")!!.absolutePath
//		val cfgDir = File(savePath, "cfg")
//		val cfgFile = File(cfgDir, "settings.cfg")
//		cfgFile.bufferedWriter().use { out ->
//			out.write(viewModel.audioCodec.value!!)
//			out.write("\n")
//			out.write(viewModel.channel.value!!)
//			out.write("\n")
//			out.write(viewModel.sampleRate.value!!)
//			out.write("\n")
//			out.write(viewModel.bitRate.value!!)
//		}
//		Toast.makeText(requireContext(), "Config saved!", Toast.LENGTH_SHORT).show()
//	}

	private fun updateDesc() {
		updateOutputFormat()
		when (viewModel.audioCodec.value) {
			0 -> binding.codecDesc.text = getString(R.string.pcm_desc)
			1 -> binding.codecDesc.text = getString(R.string.aac_desc)
			2 -> binding.codecDesc.text = getString(R.string.amr_nb_desc)
			3 -> binding.codecDesc.text = getString(R.string.amr_wb_desc)
			4 -> binding.codecDesc.text = getString(R.string.vorbis_desc)
			5 -> binding.codecDesc.text = getString(R.string.opus_desc)
		}
		val format: String = when (viewModel.audioCodec.value) {
			0 -> "WAV"
			1 -> "M4A"
			2 -> "3GP"
			3 -> "3GP"
			4 -> "WEBM"
			5 -> "OGG"
			else -> ""
		}
		val channel: String = when (viewModel.channel.value) {
			1 -> "Mono"
			2 -> "Stereo"
			else -> ""
		}
		val sampleRate: Int = viewModel.sampleRate.value!!

		val bitRate: Int =
			if (viewModel.audioCodec.value != 0) viewModel.bitRate.value!!
			else 16 * viewModel.channel.value!! * viewModel.sampleRate.value!! / 1000

		val isPcm: Boolean = (viewModel.audioCodec.value == 0)

		val fileSize: String = getSize(
			isPcm,
			viewModel.channel.value!!,
			viewModel.sampleRate.value!!,
			viewModel.bitRate.value!!
		)
		binding.avgFileSize.text = getString(
			R.string.avg_file_size,
			format,
			channel,
			sampleRate,
			bitRate,
			fileSize
		)
	}

	private fun updateOutputFormat() {
		when (viewModel.audioCodec.value) {
			0 -> binding.formatName.text = getString(R.string.wav)
			1 -> binding.formatName.text = getString(R.string.aac)
			2 -> binding.formatName.text = getString(R.string.three_gp)
			3 -> binding.formatName.text = getString(R.string.three_gp)
			4 -> binding.formatName.text = getString(R.string.webm)
			5 -> binding.formatName.text = getString(R.string.ogg)
		}
	}

	private fun getSize(
		isPcm: Boolean, channel: Int, sampleRate: Int, bitRate: Int
	): String {
		val size: Int = if (isPcm) 60 * 16 * channel * sampleRate
		else 60 * bitRate * 1000

		val df = DecimalFormat("#.##")
		df.roundingMode = RoundingMode.CEILING

		var size2: Double = size.toDouble() / 1024 / 8 // convert b to KB
		return if (size2 >= 1024) {
			size2 /= 1024
			df.format(size2) + " MB"
		} else {
			df.format(size2) + " KB"
		}
	}

	private fun updateConfigOnSelected() {
		updateConfigOnCodecSelected()
		updateConfigOnChannelSelected()
		updateConfigOnSampleRateSelected()
		updateConfigOnBitRateSelected()
	}

	private fun updateConfigOnCodecSelected() {
		when (binding.codecRadio.checkedRadioButtonId) {
			R.id.pcm_btn -> viewModel.setAudioCodec(0)
			R.id.aac_btn -> viewModel.setAudioCodec(1)
			R.id.amr_nb_btn -> viewModel.setAudioCodec(2)
			R.id.amr_wb_btn -> viewModel.setAudioCodec(3)
			R.id.vorbis_btn -> viewModel.setAudioCodec(4)
			R.id.opus_btn -> viewModel.setAudioCodec(5)
		}
		if (viewModel.audioCodec.value == 2 || viewModel.audioCodec.value == 3) {
			viewModel.setChannel(1)
			viewModel.setBitRate(12)
		}
		if (viewModel.audioCodec.value == 2) viewModel.setSampleRate(8000)
		else if (viewModel.audioCodec.value == 3) viewModel.setSampleRate(16000)
	}

	private fun updateConfigOnChannelSelected() {
		if (binding.channelRadio.checkedRadioButtonId == R.id.mono) viewModel.setChannel(
			1
		)
		else if (binding.channelRadio.checkedRadioButtonId == R.id.stereo) viewModel.setChannel(
			2
		)
	}

	private fun updateConfigOnSampleRateSelected() {
		when (binding.sampleRateRadio.checkedRadioButtonId) {
			R.id.sample_8kHz -> viewModel.setSampleRate(8000)
			R.id.sample_11kHz -> viewModel.setSampleRate(11025)
			R.id.sample_16kHz -> viewModel.setSampleRate(16000)
			R.id.sample_22kHz -> viewModel.setSampleRate(22050)
			R.id.sample_32kHz -> viewModel.setSampleRate(32000)
			R.id.sample_44kHz -> viewModel.setSampleRate(44100)
			R.id.sample_48kHz -> viewModel.setSampleRate(48000)
			R.id.sample_88kHz -> viewModel.setSampleRate(88200)
			R.id.sample_96kHz -> viewModel.setSampleRate(96000)
		}
	}

	private fun updateConfigOnBitRateSelected() {
		when (binding.bitrateRadio.checkedRadioButtonId) {
			R.id.bit_32kbps -> viewModel.setBitRate(32)
			R.id.bit_48kbps -> viewModel.setBitRate(48)
			R.id.bit_64kbps -> viewModel.setBitRate(64)
			R.id.bit_96kbps -> viewModel.setBitRate(96)
			R.id.bit_112kbps -> viewModel.setBitRate(112)
			R.id.bit_128kbps -> viewModel.setBitRate(128)
			R.id.bit_160kbps -> viewModel.setBitRate(160)
			R.id.bit_192kbps -> viewModel.setBitRate(192)
			R.id.bit_224kbps -> viewModel.setBitRate(224)
			R.id.bit_256kbps -> viewModel.setBitRate(256)
			R.id.bit_510kbps -> viewModel.setBitRate(510)
		}
	}

	private fun codecOnClicked() {
		if (binding.amrNbBtn.isChecked || binding.amrWbBtn.isChecked) {

			binding.channelRadio.clearCheck()
			binding.mono.isChecked = true

			hideAll()
			binding.sampleRateHeader.visibility = View.VISIBLE
			binding.sampleRateRadio.visibility = View.VISIBLE

			if (binding.amrNbBtn.isChecked) {
				binding.sample8kHz.visibility = View.VISIBLE
				binding.sampleRateRadio.clearCheck()
				binding.sample8kHz.isChecked = true
			} else if (binding.amrWbBtn.isChecked) {
				binding.sample16kHz.visibility = View.VISIBLE
				binding.sampleRateRadio.clearCheck()
				binding.sample16kHz.isChecked = true
			}
		} else if (binding.aacBtn.isChecked) {
			hideAll()
			binding.stereo.visibility = View.VISIBLE

			binding.channelRadio.clearCheck()
			binding.stereo.isChecked = true

			binding.sampleRateHeader.visibility = View.VISIBLE
			binding.sampleRateRadio.visibility = View.VISIBLE
			binding.sample8kHz.visibility = View.VISIBLE
			binding.sample11kHz.visibility = View.VISIBLE
			binding.sample16kHz.visibility = View.VISIBLE
			binding.sample22kHz.visibility = View.VISIBLE
			binding.sample32kHz.visibility = View.VISIBLE
			binding.sample44kHz.visibility = View.VISIBLE
			binding.sample48kHz.visibility = View.VISIBLE

			binding.sampleRateRadio.clearCheck()
			binding.sample44kHz.isChecked = true

			binding.bitrateHeader.visibility = View.VISIBLE
			binding.bitrateRadio.visibility = View.VISIBLE
			binding.bit128kbps.visibility = View.VISIBLE
			binding.bit160kbps.visibility = View.VISIBLE
			binding.bit192kbps.visibility = View.VISIBLE
			binding.bit224kbps.visibility = View.VISIBLE
			binding.bit256kbps.visibility = View.VISIBLE

			binding.bitrateRadio.clearCheck()
			binding.bit128kbps.isChecked = true
		} else if (binding.vorbisBtn.isChecked) {
			hideAll()
			binding.stereo.visibility = View.VISIBLE

			binding.channelRadio.clearCheck()
			binding.stereo.isChecked = true

			binding.sampleRateHeader.visibility = View.VISIBLE
			binding.sampleRateRadio.visibility = View.VISIBLE
			binding.sample8kHz.visibility = View.VISIBLE
			binding.sample11kHz.visibility = View.VISIBLE
			binding.sample16kHz.visibility = View.VISIBLE
			binding.sample22kHz.visibility = View.VISIBLE
			binding.sample32kHz.visibility = View.VISIBLE
			binding.sample44kHz.visibility = View.VISIBLE
			binding.sample48kHz.visibility = View.VISIBLE

			binding.sampleRateRadio.clearCheck()
			binding.sample44kHz.isChecked = true

			binding.bitrateHeader.visibility = View.VISIBLE
			binding.bitrateRadio.visibility = View.VISIBLE
			binding.bit64kbps.visibility = View.VISIBLE
			binding.bit96kbps.visibility = View.VISIBLE
			binding.bit112kbps.visibility = View.VISIBLE
			binding.bit128kbps.visibility = View.VISIBLE
			binding.bit160kbps.visibility = View.VISIBLE
			binding.bit192kbps.visibility = View.VISIBLE
			binding.bit224kbps.visibility = View.VISIBLE
			binding.bit256kbps.visibility = View.VISIBLE

			binding.bitrateRadio.clearCheck()
			binding.bit96kbps.isChecked = true
		} else if (binding.pcmBtn.isChecked) {
			hideAll()

			binding.stereo.visibility = View.VISIBLE
			binding.channelRadio.clearCheck()
			binding.stereo.isChecked = true

			binding.sampleRateHeader.visibility = View.VISIBLE
			binding.sampleRateRadio.visibility = View.VISIBLE
			binding.sample8kHz.visibility = View.VISIBLE
			binding.sample11kHz.visibility = View.VISIBLE
			binding.sample16kHz.visibility = View.VISIBLE
			binding.sample22kHz.visibility = View.VISIBLE
			binding.sample32kHz.visibility = View.VISIBLE
			binding.sample44kHz.visibility = View.VISIBLE
			binding.sample48kHz.visibility = View.VISIBLE
			binding.sample88kHz.visibility = View.VISIBLE
			binding.sample96kHz.visibility = View.VISIBLE

			binding.sampleRateRadio.clearCheck()
			binding.sample44kHz.isChecked = true
		} else if (binding.opusBtn.isChecked) {
			hideAll()

			binding.stereo.visibility = View.VISIBLE
			binding.channelRadio.clearCheck()
			binding.stereo.isChecked = true

			binding.sampleRateHeader.visibility = View.VISIBLE
			binding.sampleRateRadio.visibility = View.VISIBLE
			binding.sample8kHz.visibility = View.VISIBLE
			binding.sample11kHz.visibility = View.VISIBLE
			binding.sample16kHz.visibility = View.VISIBLE
			binding.sample22kHz.visibility = View.VISIBLE
			binding.sample32kHz.visibility = View.VISIBLE
			binding.sample44kHz.visibility = View.VISIBLE
			binding.sample48kHz.visibility = View.VISIBLE

			binding.sampleRateRadio.clearCheck()
			binding.sample48kHz.isChecked = true

			binding.bitrateHeader.visibility = View.VISIBLE
			binding.bitrateRadio.visibility = View.VISIBLE
			binding.bit32kbps.visibility = View.VISIBLE
			binding.bit48kbps.visibility = View.VISIBLE
			binding.bit64kbps.visibility = View.VISIBLE
			binding.bit96kbps.visibility = View.VISIBLE
			binding.bit112kbps.visibility = View.VISIBLE
			binding.bit128kbps.visibility = View.VISIBLE
			binding.bit160kbps.visibility = View.VISIBLE
			binding.bit192kbps.visibility = View.VISIBLE
			binding.bit224kbps.visibility = View.VISIBLE
			binding.bit256kbps.visibility = View.VISIBLE
			binding.bit510kbps.visibility = View.VISIBLE

			binding.bitrateRadio.clearCheck()
			binding.bit64kbps.isChecked = true
		}
	}

	private fun hideAll() {
		binding.stereo.visibility = View.GONE

		binding.sampleRateHeader.visibility = View.GONE
		binding.sample8kHz.visibility = View.GONE
		binding.sample11kHz.visibility = View.GONE
		binding.sample16kHz.visibility = View.GONE
		binding.sample22kHz.visibility = View.GONE
		binding.sample32kHz.visibility = View.GONE
		binding.sample44kHz.visibility = View.GONE
		binding.sample48kHz.visibility = View.GONE
		binding.sample88kHz.visibility = View.GONE
		binding.sample96kHz.visibility = View.GONE
		binding.sampleRateRadio.visibility = View.GONE

		binding.bitrateHeader.visibility = View.GONE
		binding.bit32kbps.visibility = View.GONE
		binding.bit48kbps.visibility = View.GONE
		binding.bit64kbps.visibility = View.GONE
		binding.bit96kbps.visibility = View.GONE
		binding.bit112kbps.visibility = View.GONE
		binding.bit128kbps.visibility = View.GONE
		binding.bit160kbps.visibility = View.GONE
		binding.bit192kbps.visibility = View.GONE
		binding.bit224kbps.visibility = View.GONE
		binding.bit256kbps.visibility = View.GONE
		binding.bit510kbps.visibility = View.GONE
		binding.bitrateRadio.visibility = View.GONE
	}
}
