/*
 * Copyright 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.gabc

import gnuwimp.swing.Swing
import gnuwimp.util.Task
import gnuwimp.util.getIntAt
import gnuwimp.util.safeClose
import gnuwimp.util.sumByLong
import java.io.File
import java.io.InputStream
import java.io.OutputStream

//------------------------------------------------------------------------------
data class WavProperty(val sampleRate: String, val channels: Int, val channelString: String = if (channels == 1) "mono" else "stereo")

//------------------------------------------------------------------------------
class TranscoderTask(val parameters: Parameters) : Task(max = parameters.mp3Files.sumByLong(File::length)) {
    //--------------------------------------------------------------------------
    private fun createEncodeParams(wav: WavProperty): MutableList<String> {
        val list = mutableListOf<String>()

        list.add("lame")
        list.add("--nohist")
        list.add("--disptime")
        list.add("0.2")
        list.add("-m")
        list.add("m")
        list.add("-r")
        list.add("-s")
        list.add(wav.sampleRate)
        list.add("-b")
        list.add(parameters.bitrate)

        if (wav.channels == 2) {
            list.add("-a")
        }

        list.add("--id3v2-only")
        list.add("--ta")
        list.add(parameters.author)
        list.add("--tl")
        list.add(parameters.title)
        list.add("--tt")
        list.add(parameters.title)
        list.add("--ty")
        list.add(parameters.year)
        list.add("--tg")
        list.add("Audiobook")
        list.add("--tn")
        list.add("1/1")

        if (parameters.comment.isNotBlank() == true) {
            list.add("--tc")
            list.add(parameters.comment)
        }

        if (parameters.cover.isNotBlank() == true) {
            list.add("--ti")
            list.add(parameters.cover)
        }

        list.add("-")
        list.add(parameters.mp3.absolutePath)

        return list
    }

    //--------------------------------------------------------------------------
    private fun parseWavHeader(buffer: ByteArray, size: Int): WavProperty {
        val channelIndex     = 22
        val samplerateIndex  = 24

        if (size < 50) {
            throw Exception("error: to few bytes to parse")
        }

        if (buffer[0].toChar() != 'R' || buffer[1].toChar() != 'I' || buffer[2].toChar() != 'F' || buffer[3].toChar() != 'F') {
            throw Exception("error: this is not wav data")
        }

        val channels = buffer[channelIndex].toInt()

        if (channels < 1 || channels > 2) {
            throw Exception("error: channel count ($channels) is out of range")
        }

        val samplerate = when(val samplerate = buffer.getIntAt(samplerateIndex).toInt()) {
            8000 -> "8"
            11025 ->  "11.025"
            12000 ->  "12"
            22050 ->  "22.050"
            24000 ->  "24"
            32000 ->  "32"
            44100 ->  "44.1"
            48000 ->  "48"
            64000 ->  "64"
            88200 ->  "88.2"
            96000 ->  "96"
            else -> throw Exception("error: samplerate ($samplerate) is out of range")
        }

        return WavProperty(sampleRate = samplerate, channels = channels)
    }

    //--------------------------------------------------------------------------
    override fun run() {
        var res                         = ""
        val buffer                      = ByteArray(size = 4096 * 32)
        var encoderProcess: Process?    = null
        var decoderProcess: Process?    = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream?   = null
        var firstProp                   = WavProperty("", 0)
        var encoderParams: MutableList<String>
        var encoderBuilder: ProcessBuilder?
        var decoderBuilder: ProcessBuilder?

        try {
            for (mp3 in parameters.mp3Files) {
                val decoderParams = mutableListOf<String>("lame", "--quiet", "--decode", mp3.path, "-")
                var parseHeader   = true
                var currProp: WavProperty

                Swing.logMessage = decoderParams.joinToString(separator = " ")

                decoderBuilder = ProcessBuilder(decoderParams)
                decoderProcess = decoderBuilder.start()
                inputStream    = decoderProcess.inputStream
                message        = "decoding ${mp3.path}"

                while (decoderProcess.isAlive == true) {
                    val read = inputStream.read(buffer)

                    if (read > 0) {
                        if (encoderProcess == null) {
                            firstProp        = parseWavHeader(buffer, read)
                            encoderParams    = createEncodeParams(firstProp)
                            Swing.logMessage = encoderParams.joinToString(separator = " ")
                            encoderBuilder   = ProcessBuilder(encoderParams)
                            encoderProcess   = encoderBuilder.start()
                            outputStream     = encoderProcess.outputStream
                        }
                        else if (parseHeader == true) {
                            currProp = parseWavHeader(buffer, read)

                            if (currProp != firstProp) {
                                throw Exception("error: channels or samplerate are different for these tracks\n${parameters.mp3Files[0].name} (${firstProp.sampleRate}Khz, ${firstProp.channelString})\n${mp3.name} (${currProp.sampleRate}Khz, ${currProp.channelString})")
                            }
                        }

                        if (parseHeader == true) {
                            parseHeader = false
                            outputStream?.write(buffer, 44, read)
                        }
                        else {
                            outputStream?.write(buffer, 0, read)
                        }
                    }

                    if (abort == true) {
                        throw Exception("error: user abort")
                    }
                }

                progress += mp3.length()

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

            if (res == "" && decoderProcess != null && decoderProcess.exitValue() != 0) {
                res = "error: decoder failed"
            }

            if (res == "" && encoderProcess != null && encoderProcess.exitValue() != 0) {
                res = "error: encoder failed"
            }

            if (res.isNotEmpty() == true) {
                throw Exception(res)
            }
        }
    }
}
