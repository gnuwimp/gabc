/*
 * Copyright 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.swing.Swing
import gnuwimp.util.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream

//------------------------------------------------------------------------------
data class WavProperty(val sampleRateString: String, val sampleRate: Int, val channels: Short, val channelString: String = if (channels == 1.toShort()) "mono" else "stereo", val bitWidth: Short, val data: Int)

//------------------------------------------------------------------------------
class TranscoderTask(val parameters: Parameters) : Task(max = parameters.audioFiles.sumByLong(File::length)) {
    //--------------------------------------------------------------------------
    private fun createEncodeParams(wav: WavProperty): MutableList<String> {
        val list = mutableListOf<String>()

        list.add("lame")
        list.add("--quiet")

        if (wav.channels == 1.toShort()) {
            list.add("-m")
            list.add("m")
        }
        else if (parameters.mono == true && wav.channels == 2.toShort()) {
            list.add("-m")
            list.add("m")
            list.add("-a")
        }

        list.add("-r")
        list.add("-s")
        list.add(wav.sampleRateString)

        if (parameters.vbr == true) {
            list.add("-V")
            list.add("0")
            list.add("-B")
            list.add(parameters.bitrate)
            list.add("--bitwidth")
            list.add("${wav.bitWidth}")
        }
        else {
            list.add("--cbr")
            list.add("-b")
            list.add(parameters.bitrate)
        }

        list.add("-")
        list.add(parameters.mp3.absolutePath)

        return list
    }

    //--------------------------------------------------------------------------
    private fun parseWavHeader(buffer: ByteArray, size: Int): WavProperty {
        if (size < 50) {
            throw Exception("error: to few bytes to parse the wav header")
        }

        if (buffer[0].toInt().toChar() != 'R' || buffer[1].toInt().toChar() != 'I' || buffer[2].toInt().toChar() != 'F' || buffer[3].toInt().toChar() != 'F') {
            throw Exception("error: this is not wav data")
        }

        val channels   = buffer[22].toShort()
        val samplerate = buffer.getIntAt(24).toInt()
        val bitwidth   = buffer[34].toShort()

        if (channels < 1 || channels > 2) {
            throw Exception("error: channel count ($channels) is out of range")
        }

        if (bitwidth != 8.toShort() && bitwidth != 16.toShort() && bitwidth != 24.toShort() && bitwidth != 32.toShort()) {
            throw Exception("error: bitwidth ($bitwidth) is out of range")
        }

        val samplerateString = when(samplerate) {
            8000 -> "8"
            11025 -> "11.025"
            12000 -> "12"
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
            else -> throw Exception("error: samplerate ($samplerate) is out of range")
        }

        var data = 44

        for (f in 30 .. (buffer.size - 8)) {
            if (buffer[f].toInt().toChar() == 'd' && buffer[f + 1].toInt().toChar() == 'a' && buffer[f + 2].toInt().toChar() == 't' && buffer[f + 3].toInt().toChar() == 'a') {
                data = f + 8
                break
            }
        }

        return WavProperty(sampleRateString = samplerateString, sampleRate = samplerate, channels = channels, bitWidth = bitwidth, data = data)
    }

    //--------------------------------------------------------------------------
    override fun run() {
        var res                         = ""
        val buffer                      = ByteArray(size = 8192)
        var encoderProcess: Process?    = null
        var decoderProcess: Process?    = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream?   = null
        var firstProp                   = WavProperty(sampleRateString = "", sampleRate = 0, channels = 0, channelString = "", bitWidth = 0, data = 0)
        var encoderParams: MutableList<String>
        var encoderBuilder: ProcessBuilder?
        var decoderBuilder: ProcessBuilder?

        try {
            var gap: ByteArray? = null

            for (file in parameters.audioFiles) {
                val decoderParams = if (file.extension.lowercase() == "mp3") listOf<String>("lame", "--quiet", "--decode", file.path, "-") else listOf<String>("ffmpeg", "-loglevel", "level+quiet", "-i", file.path, "-f", "wav", "-")
                var parseHeader   = true
                var currProp      = WavProperty(sampleRateString = "", sampleRate = 0, channels = 0, channelString = "", bitWidth = 0, data = 0)

                Swing.logMessage = decoderParams.joinToString(separator = " ")

                decoderBuilder = ProcessBuilder(decoderParams)
                decoderProcess = decoderBuilder.start()
                inputStream    = decoderProcess.inputStream
                message        = "decoding ${file.path}"

                while (decoderProcess.isAlive == true) {
                    val read = inputStream.read(buffer)

                    if (read > 0) {
                        if (encoderProcess == null) {
                            firstProp        = parseWavHeader(buffer, read)
                            currProp         = firstProp
                            encoderParams    = createEncodeParams(firstProp)
                            Swing.logMessage = encoderParams.joinToString(separator = " ")
                            encoderBuilder   = ProcessBuilder(encoderParams)
                            encoderProcess   = encoderBuilder.start()
                            outputStream     = encoderProcess.outputStream

                            val seconds = parameters.gap.numOrZero.toInt()

                            if (seconds != 0) {
                                gap = ByteArray(size = (firstProp.sampleRate * firstProp.channels * 2 * seconds))
                            }
                        }
                        else if (parseHeader == true) {
                            currProp = parseWavHeader(buffer, read)
                        }

                        if (currProp.sampleRate != firstProp.sampleRate || currProp.channels != firstProp.channels || currProp.bitWidth != firstProp.bitWidth) {
                            throw Exception("error: channels or samplerate or bitwidth are different for these tracks\n${parameters.audioFiles[0].name} (${firstProp.sampleRateString} Khz, ${firstProp.channelString}, ${firstProp.bitWidth} bit)\n${file.name} (${currProp.sampleRateString} Khz, ${currProp.channelString}, ${currProp.bitWidth} bit)")
                        }

                        if (parseHeader == true) {
                            parseHeader = false

                            if (read > currProp.data) {
                                outputStream?.write(buffer, currProp.data, read - currProp.data)
                            }
                        }
                        else if (read > 0){
                            outputStream?.write(buffer, 0, read)
                        }
                    }

                    if (abort == true) {
                        throw Exception("error: user abort")
                    }
                }

                if (gap != null && file != parameters.audioFiles.last()) {
                    outputStream?.write(gap, 0, gap.size)
                }

                progress += file.length()

                if (decoderProcess.exitValue() != 0) {
                    throw Exception("")
                }

                inputStream.safeClose()
                decoderProcess.waitFor()

                inputStream    = null
                decoderProcess = null
            }
        }
        catch (e: Exception) {
            val msg = e.message

            if (msg != null && msg.startsWith(prefix = "error:") == true) {
                res = msg
            }
        }
        finally {
            inputStream?.safeClose()
            outputStream?.safeClose()

            if (decoderProcess?.isAlive == true) {
                decoderProcess.waitFor()
            }

            if (encoderProcess?.isAlive == true) {
                encoderProcess.waitFor()
            }

            if (res == "" && encoderProcess != null && encoderProcess.exitValue() != 0) {
                res = "error: encoder failed"
            }

            if (res == "" && decoderProcess != null && decoderProcess.exitValue() != 0) {
                res = "error: decoder failed"
            }

            if (res.isNotEmpty() == true) {
                throw Exception(res)
            }
        }
    }
}
