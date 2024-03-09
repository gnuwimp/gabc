/*
 * Copyright 2016 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.getIntAt

//------------------------------------------------------------------------------
class WavHeader {
    companion object {
        const val MONO   = 1.toShort()
        const val STEREO = 2.toShort()
    }

    val sampleRateString: String
    val sampleRateString2: String
    val sampleRate: Int
    val channels: Short
    val channelString: String
    val bitWidth: Short
    val data: Int

    //--------------------------------------------------------------------------
    constructor() {
        sampleRate = 0
        sampleRateString = ""
        sampleRateString2 = ""
        channels = 0
        channelString = ""
        bitWidth = 0
        data = 0
    }

    //--------------------------------------------------------------------------
    constructor(buffer: ByteArray, size: Int) {
        if (size < 50) {
            throw Exception("error: to few bytes to parse the wav header")
        }

        if (buffer[0].toInt().toChar() != 'R' || buffer[1].toInt().toChar() != 'I' || buffer[2].toInt().toChar() != 'F' || buffer[3].toInt().toChar() != 'F') {
            throw Exception("error: this is not wav data")
        }

        channels   = buffer[22].toShort()
        sampleRate = buffer.getIntAt(24).toInt()
        bitWidth   = buffer[34].toShort()

        if (channels < 1 || channels > 2) {
            throw Exception("error: channel count ($channels) is out of range")
        }

        channelString = if (channels == 1.toShort()) "mono" else "stereo"

        if (bitWidth != 8.toShort() && bitWidth != 16.toShort() && bitWidth != 24.toShort() && bitWidth != 32.toShort()) {
            throw Exception("error: bitwidth ($bitWidth) is out of range")
        }

        sampleRateString = when(sampleRate) {
            8000 -> "8"
            11025 -> "11.025"
            12000 -> "12"
            16000 -> "16"
            22050 -> "22.050"
            24000 -> "24"
            32000 -> "32"
            37800 -> "37.8"
            44056 -> "44.056"
            44100 -> "44.1"
            48000 -> "48"
            64000 -> "64"
            88200 -> "88.2"
            96000 -> "96"
            176400 -> "176.4"
            192000 -> "192"
            else -> throw Exception("error: samplerate ($sampleRate) is out of range")
        }

        sampleRateString2 = when(sampleRate) {
            8000 -> "8000"
            11025 -> "11025"
            12000 -> "12000"
            16000 -> "16000"
            22050 -> "22050"
            24000 -> "24000"
            32000 -> "32000"
            37800 -> "37800"
            44056 -> "44056"
            44100 -> "44100"
            48000 -> "48000"
            64000 -> "64000"
            88200 -> "88200"
            96000 -> "96000"
            176400 -> "176400"
            else -> "192000"
        }

        var dataPos = 44

        for (f in 30 .. (buffer.size - 8)) {
            if (buffer[f].toInt().toChar() == 'd' && buffer[f + 1].toInt().toChar() == 'a' && buffer[f + 2].toInt().toChar() == 't' && buffer[f + 3].toInt().toChar() == 'a') {
                dataPos = f + 8
                break
            }
        }

        data = dataPos
    }
}