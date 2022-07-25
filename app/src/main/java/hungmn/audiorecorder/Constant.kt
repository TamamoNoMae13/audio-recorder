package hungmn.audiorecorder

class Constant {
	enum class Codec(val codec: Int) {
		PCM(1),
		AAC(2),
		AMR_NB(4),
		AMR_WB(8),
		OPUS(16)
	}

	enum class FFMpegCodec(val codec: String) {
		AAC("aac"),
		OPUS("libopus"),
		VORBIS("libvorbis"),

	}

	enum class Format(val format: String) {
		WAV("wav"),
		M4A("m4a"),
		THREE_GP("3gp"),
		OPUS("opus")
	}

	enum class Channel(val channel: Int) {
		MONO(1),
		STEREO(2)
	}

	enum class KBitRate(val kBitRate: Int) {
		NONE(0),
		ONE_HUNDRED_TWENTY_EIGHT(128000),
		ONE_HUNDRED_SIXTY(160000),
		ONE_HUNDRED_NINETY_TWO(192000),
		TWO_HUNDRED_TWENTY_FOUR(224000),
		TWO_HUNDRED_FIFTY_SIX(256000)
	}

	enum class SampleRate(val sampleRate: Int) {
		EIGHT_THOUSAND(8000),
		ELEVEN_THOUSAND(11025),
		SIXTEEN_THOUSAND(16000),
		TWENTY_TWO_THOUSAND(22050),
		THIRTY_TWO_THOUSAND(32000),
		FORTY_FOUR_THOUSAND(44100),
		FORTY_EIGHT_THOUSAND(48000),
		SIXTY_EIGHT_THOUSAND(96000)
	}
}
