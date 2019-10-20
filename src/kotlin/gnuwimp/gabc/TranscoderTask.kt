/*
 * Copyright 2019 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.gabc

import gnuwimp.core.extension.getIntAt
import gnuwimp.core.extension.safeClose
import gnuwimp.core.extension.sumByLong
import gnuwimp.core.swing.Platform
import gnuwimp.core.util.Task
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Transcode a bunch of mp3s
 */
class TranscoderTask(val parameters: Parameters) : Task(max = parameters.mp3Files.sumByLong(File::length)) {
    /**
     * Create encoder parameters
     */
    private fun createEncodeParams(sampleRate: String, list: MutableList<String>) {
        list.add("lame")
        list.add("--nohist")
        list.add("--disptime")
        list.add("0.2")
        list.add("-m")
        list.add("m")
        list.add("-r")
        list.add("-s")
        list.add(sampleRate)
        list.add("-b")
        list.add(parameters.bitrate)
        list.add("-a")

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

        if (!parameters.comment.isBlank()) {
            list.add("--tc")
            list.add(parameters.comment)
        }

        if (parameters.cover.isNotBlank()) {
            list.add("--ti")
            list.add(parameters.cover)
        }

        list.add("-")
        list.add(parameters.mp3.absolutePath)
    }

    /**
     * Parse wav header and look for number of channels and samplerate
     */
    private fun parseWavHeader(buffer: ByteArray, size: Int): Pair<String, String> {
        val channelIndex = 22
        val samplerateIndex = 24
        val channels: Int
        val samplerateString: String

        if (size < 50)
            throw Exception("error: to few bytes to parse")

        if (buffer[0].toChar() != 'R' || buffer[1].toChar() != 'I' || buffer[2].toChar() != 'F' || buffer[3].toChar() != 'F')
            throw Exception("error: this is not wav data")

        channels = buffer[channelIndex].toInt()
        if (channels < 1 || channels > 2)
            throw Exception("error: channel count ($channels) is out of range")

        val samplerate = buffer.getIntAt(samplerateIndex).toInt()
        samplerateString = when(samplerate) {
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

        return Pair("$channels", samplerateString)
    }

    /**
     * Run until all files has been decompressed and sent to compresser
     * If something failes and exceptions is throwed
     */
    override fun run() {
        var res = ""
        val encoderParams = mutableListOf<String>()
        val buffer = ByteArray(size = 4096 * 32)
        var encoderBuilder: ProcessBuilder?
        var decoderBuilder: ProcessBuilder?
        var encoderProcess: Process? = null
        var decoderProcess: Process? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        var firstProp = Pair("", "")

        try {
            for (mp3 in parameters.mp3Files) {
                val decoderParams = mutableListOf<String>("lame", "--quiet", "--decode", mp3.path, "-")
                var parseHeader = true
                var currProp: Pair<String, String>

                Platform.logMessage = decoderParams.joinToString(separator = " ")

                decoderBuilder = ProcessBuilder(decoderParams)
                decoderProcess = decoderBuilder.start()
                inputStream = decoderProcess.inputStream
                message = "decoding ${mp3.path}"

                while (decoderProcess.isAlive) {
                    val read = inputStream.read(buffer)

                    if (read > 0) {
                        if (encoderProcess == null) {
                            firstProp = parseWavHeader(buffer, read)
                            createEncodeParams(firstProp.second, encoderParams)
                            Platform.logMessage = encoderParams.joinToString(separator = " ")
                            encoderBuilder = ProcessBuilder(encoderParams)
                            encoderProcess = encoderBuilder.start()
                            outputStream = encoderProcess.outputStream
                        }
                        else if (parseHeader) {
                            currProp = parseWavHeader(buffer, read)

                            if (currProp != firstProp)
                                throw Exception("error: channels or samplerate are different for the first track (${parameters.mp3Files[0].name}) $firstProp and this track $currProp (${mp3.name})")
                        }

                        if (parseHeader) {
                            parseHeader = false
                            outputStream?.write(buffer, 44, read)
                        }
                        else
                            outputStream?.write(buffer, 0, read)
                    }

                    if (abort)
                        throw Exception("error: user abort")
                }

                progress += mp3.length()

                if (decoderProcess.exitValue() != 0)
                    throw Exception("")

                inputStream.safeClose()
                decoderProcess.waitFor()
                inputStream = null
                decoderProcess = null
            }
        }
        catch (e: Exception) {
            val msg = e.message

            if (msg != null && msg.startsWith(prefix = "error:"))
                res = msg
        }
        finally {
            inputStream?.safeClose()
            outputStream?.safeClose()

            if (decoderProcess?.isAlive == true)
                decoderProcess.waitFor()

            if (encoderProcess?.isAlive == true)
                encoderProcess.waitFor()

            if (res == "" && decoderProcess != null && decoderProcess.exitValue() != 0)
                res = "error: decoder failed"

            if (res == "" && encoderProcess != null && encoderProcess.exitValue() != 0)
                res = "error: encoder failed"

            if (res.isNotEmpty())
                throw Exception(res)
        }
    }
}
