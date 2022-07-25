package hungmn.audiorecorder.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import hungmn.audiorecorder.Constant
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
			val a: Constant.Codec = viewModel.audioCodec.value!!
			val b: Constant.Channel = viewModel.channel.value!!
			val c: Constant.SampleRate = viewModel.sampleRate.value!!
			val d: Constant.KBitRate = viewModel.bitRate.value!!
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
		binding.codecDesc.text = when (viewModel.audioCodec.value) {
			Constant.Codec.AAC ->  getString(R.string.aac_desc)
			Constant.Codec.AMR_NB -> getString(R.string.amr_nb_desc)
			Constant.Codec.AMR_WB -> getString(R.string.amr_wb_desc)
			Constant.Codec.OPUS -> getString(R.string.opus_desc)
			Constant.Codec.PCM -> getString(R.string.pcm_desc)
			else -> ""
		}
		val format: String = when (viewModel.audioCodec.value) {
			Constant.Codec.AAC -> Constant.Format.M4A.format
			Constant.Codec.AMR_NB -> Constant.Format.THREE_GP.format
			Constant.Codec.AMR_WB -> Constant.Format.THREE_GP.format
			Constant.Codec.OPUS -> Constant.Format.OPUS.format
			else -> Constant.Format.WAV.format
		}
		val channel: String = when (viewModel.channel.value) {
			Constant.Channel.MONO -> "Mono"
			Constant.Channel.STEREO -> "Stereo"
			else -> ""
		}
		val sampleRate: Int = viewModel.sampleRate.value!!.sampleRate

		val bitRate: Int =
			if (viewModel.audioCodec.value != Constant.Codec.PCM)
				viewModel.bitRate.value!!.kBitRate
			else 16 * viewModel.channel.value!!.channel *
					viewModel.sampleRate.value!!.sampleRate / 1000

		val isPcm: Boolean = (viewModel.audioCodec.value!! == Constant.Codec.PCM)

		val fileSize: String = getSize(
			isPcm,
			viewModel.channel.value!!.channel,
			viewModel.sampleRate.value!!.sampleRate,
			viewModel.bitRate.value!!.kBitRate
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
		binding.formatName.text = when (viewModel.audioCodec.value) {
			Constant.Codec.AAC -> Constant.Format.M4A.format
			Constant.Codec.AMR_NB -> Constant.Format.THREE_GP.format
			Constant.Codec.AMR_WB -> Constant.Format.THREE_GP.format
			Constant.Codec.OPUS -> Constant.Format.OPUS.format
			Constant.Codec.PCM -> Constant.Format.WAV.format
			else -> ""
		}
	}

	private fun getSize(
		isPcm: Boolean, channel: Int, sampleRate: Int, bitRate: Int
	): String {
		val size: Int = if (isPcm) 60 * 16 * channel * sampleRate
		else 60 * bitRate * 1000

		val df = DecimalFormat("#.##")
		df.roundingMode = RoundingMode.CEILING

		var size2: Double = size.toDouble() / 1024 / 8 // convert b to KiB
		return if (size2 >= 1024) {
			size2 /= 1024
			df.format(size2) + " MiB"
		} else {
			df.format(size2) + " KiB"
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
			R.id.pcm_btn -> viewModel.setAudioCodec(Constant.Codec.PCM)
			R.id.aac_btn -> viewModel.setAudioCodec(Constant.Codec.AAC)
			R.id.amr_nb_btn -> viewModel.setAudioCodec(Constant.Codec.AMR_NB)
			R.id.amr_wb_btn -> viewModel.setAudioCodec(Constant.Codec.AMR_WB)
			R.id.opus_btn -> viewModel.setAudioCodec(Constant.Codec.OPUS)
		}
		if (viewModel.audioCodec.value == Constant.Codec.AMR_NB ||
			viewModel.audioCodec.value == Constant.Codec.AMR_WB) {
			viewModel.setChannel(Constant.Channel.MONO)
//			viewModel.setBitRate(Constant.KBitRate.ONE_HUNDRED_TWENTY_EIGHT)
		}
		if (viewModel.audioCodec.value == Constant.Codec.AMR_NB)
			viewModel.setSampleRate(Constant.SampleRate.EIGHT_THOUSAND)
		else if (viewModel.audioCodec.value == Constant.Codec.AMR_WB)
			viewModel.setSampleRate(Constant.SampleRate.SIXTEEN_THOUSAND)
	}

	private fun updateConfigOnChannelSelected() {
		if (binding.channelRadio.checkedRadioButtonId == R.id.mono)
			viewModel.setChannel(Constant.Channel.MONO)
		else if (binding.channelRadio.checkedRadioButtonId == R.id.stereo)
			viewModel.setChannel(Constant.Channel.STEREO)
	}

	private fun updateConfigOnSampleRateSelected() {
		when (binding.sampleRateRadio.checkedRadioButtonId) {
			R.id.sample_8kHz -> viewModel.setSampleRate(Constant.SampleRate.EIGHT_THOUSAND)
			R.id.sample_11kHz -> viewModel.setSampleRate(Constant.SampleRate.ELEVEN_THOUSAND)
			R.id.sample_16kHz -> viewModel.setSampleRate(Constant.SampleRate.SIXTEEN_THOUSAND)
			R.id.sample_22kHz -> viewModel.setSampleRate(Constant.SampleRate.TWENTY_TWO_THOUSAND)
			R.id.sample_32kHz -> viewModel.setSampleRate(Constant.SampleRate.THIRTY_TWO_THOUSAND)
			R.id.sample_44kHz -> viewModel.setSampleRate(Constant.SampleRate.FORTY_FOUR_THOUSAND)
			R.id.sample_48kHz -> viewModel.setSampleRate(Constant.SampleRate.FORTY_EIGHT_THOUSAND)
		}
	}

	private fun updateConfigOnBitRateSelected() {
		when (binding.bitrateRadio.checkedRadioButtonId) {
			R.id.bit_128kbps -> viewModel.setBitRate(Constant.KBitRate.ONE_HUNDRED_TWENTY_EIGHT)
			R.id.bit_160kbps -> viewModel.setBitRate(Constant.KBitRate.ONE_HUNDRED_SIXTY)
			R.id.bit_192kbps -> viewModel.setBitRate(Constant.KBitRate.ONE_HUNDRED_NINETY_TWO)
			R.id.bit_224kbps -> viewModel.setBitRate(Constant.KBitRate.TWO_HUNDRED_TWENTY_FOUR)
			R.id.bit_256kbps -> viewModel.setBitRate(Constant.KBitRate.TWO_HUNDRED_FIFTY_SIX)
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
			binding.bit128kbps.visibility = View.VISIBLE
			binding.bit160kbps.visibility = View.VISIBLE
			binding.bit192kbps.visibility = View.VISIBLE
			binding.bit224kbps.visibility = View.VISIBLE
			binding.bit256kbps.visibility = View.VISIBLE

			binding.bitrateRadio.clearCheck()
			binding.bit128kbps.isChecked = true
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
		binding.sampleRateRadio.visibility = View.GONE

		binding.bitrateHeader.visibility = View.GONE
		binding.bit128kbps.visibility = View.GONE
		binding.bit160kbps.visibility = View.GONE
		binding.bit192kbps.visibility = View.GONE
		binding.bit224kbps.visibility = View.GONE
		binding.bit256kbps.visibility = View.GONE
		binding.bitrateRadio.visibility = View.GONE
	}
}
