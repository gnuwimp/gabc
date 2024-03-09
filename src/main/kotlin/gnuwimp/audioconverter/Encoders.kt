/*
 * Copyright 2021 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo

//------------------------------------------------------------------------------
enum class Encoders(index: Int, name: String, ext: String, exe: String, vararg params: String) {
    MP3_CBR_32(0, "MP3 CBR 32 Kbps", "mp3", "lame", "-b", "32", "--cbr"),
    MP3_CBR_48(1, "MP3 CBR 48 Kbps", "mp3", "lame", "-b", "48", "--cbr"),
    MP3_CBR_64(2, "MP3 CBR 64 Kbps", "mp3", "lame", "-b", "64", "--cbr"),
    MP3_CBR_96(3, "MP3 CBR 96 Kbps", "mp3", "lame", "-b", "96", "--cbr"),
    MP3_CBR_128(4, "MP3 CBR 128 Kbps", "mp3", "lame", "-b", "128", "--cbr"),
    MP3_CBR_192(5, "MP3 CBR 192 Kbps", "mp3", "lame", "-b", "192", "--cbr"),
    MP3_CBR_256(6, "MP3 CBR 256 Kbps", "mp3", "lame", "-b", "256", "--cbr"),
    MP3_CBR_320(7, "MP3 CBR 320 Kbps", "mp3", "lame", "--preset", "insane"),

    MP3_VBR_160(8, "MP3 VBR ~160 Kbps", "mp3", "lame", "--preset", "medium"),
    MP3_VBR_190(9, "MP3 VBR ~190 Kbps", "mp3", "lame", "--preset", "standard"),
    MP3_VBR_240(10, "MP3 VBR ~240 Kbps", "mp3", "lame", "--preset", "extreme"),

    OGG_45(11, "Ogg ~45 Kbps", "ogg", "oggenc", "-q-1"),
    OGG_64(12, "Ogg ~64 Kbps", "ogg", "oggenc", "-q0"),
    OGG_96(13, "Ogg ~96 Kbps", "ogg", "oggenc", "-q2"),
    OGG_128(14, "Ogg ~128 Kbps", "ogg", "oggenc", "-q4"),
    OGG_192(15, "Ogg ~192 Kbps", "ogg", "oggenc", "-q6"),
    OGG_256(16, "Ogg ~256 Kbps", "ogg", "oggenc", "-q8"),
    OGG_320(17, "Ogg ~320 Kbps", "ogg", "oggenc", "-q9"),
    OGG_500(18, "Ogg ~500 Kbps", "ogg", "oggenc", "-q10"),

    AAC_CBR_48(19, "AAC HE/CBR 48 Kbps", "m4a", "qaac64", "--he", "--cbr", "48"),
    AAC_CBR_80(20, "AAC HE/CBR 80 Kbps", "m4a", "qaac64", "--he", "--cbr", "80"),
    AAC_CVBR_96(21, "AAC CVBR ~96 Kbps", "m4a", "qaac64", "--cvbr", "96"),
    AAC_TVBR_128(22, "AAC TVBR63 ~128 Kbps", "m4a", "qaac64", "--tvbr", "63"),
    AAC_TVBR_256(23, "AAC TVBR109 ~256 Kbps", "m4a", "qaac64", "--tvbr", "109"),
    AAC_TVBR_320(24, "AAC TVBR127 ~320 Kbps", "m4a", "qaac64", "--tvbr", "127"),
    AAC_ALAC(25, "AAC ALAC", "m4a", "qaac64", "--alac");

    var encoderIndex = index
    var encoderName  = name
    var encoderExe   = exe
    val encoderArg   = mutableListOf<String>()
    var fileExt      = ext

    init {
        params.forEach {
            encoderArg.add(it)
        }
    }
    companion object {
        private val LAST = AAC_ALAC
        val DEFAULT = MP3_CBR_128

        //--------------------------------------------------------------------------
        fun createEncoder(par: Tab1Parameters, wavHeader: WavHeader): List<String> {
            return if (par.encoder.fileExt == "mp3") {
                createMP3Encoder(par, wavHeader)
            }
            else return if (par.encoder.fileExt == "m4a") {
                createAACEncoder(par, wavHeader)
            }
            else {
                createOGGEncoder(par, wavHeader)
            }
        }

        //--------------------------------------------------------------------------
        fun createEncoder(par: Tab2Parameters, wavHeader: WavHeader, file: FileInfo): List<String> {
            return if (par.encoder.fileExt == "mp3") {
                createMP3Encoder(par, wavHeader, file)
            }
            else return if (par.encoder.fileExt == "m4a") {
                createAACEncoder(par, wavHeader, file)
            }
            else {
                createOGGEncoder(par, wavHeader, file)
            }
        }

        //--------------------------------------------------------------------------
        private fun createAACEncoder(par: Tab1Parameters, wavHeader: WavHeader): List<String> {
            val list = mutableListOf<String>()

            list.add(par.encoder.encoderExe)
            list.add("--silent")

            list.add("--raw")
            list.add("--raw-channels")

            if (wavHeader.channels == WavHeader.MONO) {
                list.add("1")
            }
            else {
                list.add("2")
            }

            list.add("--raw-rate")
            list.add(wavHeader.sampleRateString2)

            list.add("--raw-format")
            list.add("S${wavHeader.bitWidth}L")

            par.encoder.encoderArg.forEach {
                list.add(it)
            }

            list.add("-")
            list.add("-o")
            list.add(par.outputFile.absolutePath)

            return list
        }

        //--------------------------------------------------------------------------
        private fun createAACEncoder(par: Tab2Parameters, wavHeader: WavHeader, file: FileInfo): List<String> {
            val list = mutableListOf<String>()

            list.add(par.encoder.encoderExe)
            list.add("--silent")
            list.add("--raw")
            list.add("--raw-channels")

            if (wavHeader.channels == WavHeader.MONO) {
                list.add("1")
            }
            else {
                list.add("2")
            }

            list.add("--raw-rate")
            list.add(wavHeader.sampleRateString2)

            list.add("--raw-format")
            list.add("S${wavHeader.bitWidth}L")

            par.encoder.encoderArg.forEach {
                list.add(it)
            }

            list.add("-")
            list.add("-o")
            list.add(file.canonicalPath)

            return list
        }

        //--------------------------------------------------------------------------
        private fun createMP3Encoder(par: Tab1Parameters, wavHeader: WavHeader): List<String> {
            val list = mutableListOf<String>()

            list.add(par.encoder.encoderExe)
            list.add("--quiet")

            if (wavHeader.channels == WavHeader.MONO) {
                list.add("-m")
                list.add("m")
            }
//            else if (par.mono == true && wavHeader.channels == WavHeader.STEREO) {
//                list.add("-m")
//                list.add("m")
//                list.add("-a")
//            }

            list.add("-r")
            list.add("-s")
            list.add(wavHeader.sampleRateString)

            list.add("--bitwidth")
            list.add("${wavHeader.bitWidth}")

            par.encoder.encoderArg.forEach {
                list.add(it)
            }

            list.add("-")
            list.add(par.outputFile.absolutePath)

            return list
        }

        //--------------------------------------------------------------------------
        private fun createMP3Encoder(par: Tab2Parameters, wavHeader: WavHeader, file: FileInfo): List<String> {
            val list = mutableListOf<String>()

            list.add(par.encoder.encoderExe)
            list.add("--quiet")

            if (wavHeader.channels == WavHeader.MONO) {
                list.add("-m")
                list.add("m")
            }

            list.add("-r")
            list.add("-s")
            list.add(wavHeader.sampleRateString)

            list.add("--bitwidth")
            list.add("${wavHeader.bitWidth}")

            par.encoder.encoderArg.forEach {
                list.add(it)
            }

            list.add("-")
            list.add(file.canonicalPath)

            return list
        }

        //--------------------------------------------------------------------------
        private fun createOGGEncoder(par: Tab1Parameters, wavHeader: WavHeader): List<String> {
            val list = mutableListOf<String>()

            list.add(par.encoder.encoderExe)
            list.add("--quiet")

            list.add("-r")

            list.add("-B")
            list.add("${wavHeader.bitWidth}")

            list.add("-C")
            list.add("${wavHeader.channels}")

            list.add("-R")
            list.add("${wavHeader.sampleRate}")

            par.encoder.encoderArg.forEach {
                list.add(it)
            }

//            if (par.mono == true && wavHeader.channels == WavHeader.STEREO) {
//                list.add("--downmix")
//            }

            list.add("-o")
            list.add(par.outputFile.absolutePath)
            list.add("-")

            return list
        }

        //--------------------------------------------------------------------------
        private fun createOGGEncoder(par: Tab2Parameters, wavHeader: WavHeader, file: FileInfo): List<String> {
            val list = mutableListOf<String>()

            list.add(par.encoder.encoderExe)
            list.add("--quiet")

            list.add("-r")

            list.add("-B")
            list.add("${wavHeader.bitWidth}")

            list.add("-C")
            list.add("${wavHeader.channels}")

            list.add("-R")
            list.add("${wavHeader.sampleRate}")

            par.encoder.encoderArg.forEach {
                list.add(it)
            }

            list.add("-o")
            list.add(file.canonicalPath)
            list.add("-")

            return list
        }

        //----------------------------------------------------------------------
        fun toEncoder(index: Int): Encoders {
            return when(index) {
                MP3_CBR_32.encoderIndex -> MP3_CBR_32
                MP3_CBR_48.encoderIndex -> MP3_CBR_48
                MP3_CBR_64.encoderIndex -> MP3_CBR_64
                MP3_CBR_96.encoderIndex -> MP3_CBR_96
                MP3_CBR_128.encoderIndex -> MP3_CBR_128
                MP3_CBR_192.encoderIndex -> MP3_CBR_192
                MP3_CBR_256.encoderIndex -> MP3_CBR_256
                MP3_CBR_320.encoderIndex -> MP3_CBR_320
                MP3_VBR_160.encoderIndex -> MP3_VBR_160
                MP3_VBR_190.encoderIndex -> MP3_VBR_190
                MP3_VBR_240.encoderIndex  -> MP3_VBR_240
                OGG_45.encoderIndex -> OGG_45
                OGG_64.encoderIndex -> OGG_64
                OGG_96.encoderIndex -> OGG_96
                OGG_128.encoderIndex -> OGG_128
                OGG_192.encoderIndex -> OGG_192
                OGG_256.encoderIndex -> OGG_256
                OGG_320.encoderIndex -> OGG_320
                OGG_500.encoderIndex -> OGG_500
                AAC_CBR_48.encoderIndex -> AAC_CBR_48
                AAC_CBR_80.encoderIndex -> AAC_CBR_80
                AAC_CVBR_96.encoderIndex -> AAC_CVBR_96
                AAC_TVBR_128.encoderIndex -> AAC_TVBR_128
                AAC_TVBR_256.encoderIndex -> AAC_TVBR_256
                AAC_TVBR_320.encoderIndex -> AAC_TVBR_320
                AAC_ALAC.encoderIndex -> AAC_ALAC
                else -> throw Exception("error: encoder index is out of range ($index)\nvalid values are from 0 to ${LAST.encoderIndex}")
            }
        }

        //----------------------------------------------------------------------
        val toHelp: String
            get() {
                var res = ""

                for (f in 0 .. LAST.encoderIndex) {
                    val e = toEncoder(f)
                    res += "                             ${e.encoderIndex} = ${e.encoderName}\n"
                }

                return res
            }

        //----------------------------------------------------------------------
        val toNames: List<String>
            get() {
            val res = mutableListOf<String>()

            for (f in 0 .. LAST.encoderIndex) {
                res.add(toEncoder(f).encoderName)
            }

            return res
        }
    }
}
