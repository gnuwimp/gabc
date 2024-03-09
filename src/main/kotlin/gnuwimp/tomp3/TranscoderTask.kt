/*
 * Copyright 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.swing.Swing
import gnuwimp.util.Task
import gnuwimp.util.numOrZero
import gnuwimp.util.safeClose
import gnuwimp.util.sumByLong
import java.io.File
import java.io.InputStream
import java.io.OutputStream

//------------------------------------------------------------------------------
class TranscoderTask(val parameters: Parameters) : Task(max = parameters.audioFiles.sumByLong(File::length)) {
    //--------------------------------------------------------------------------
    override fun run() {
        var res                         = ""
        var res2                        = ""
        val buffer                      = ByteArray(size = 8192)
        var encoderProcess: Process?    = null
        var decoderProcess: Process?    = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream?   = null
        var firstProp                   = Wav()
        var encoderParams: List<String>
        var encoderBuilder: ProcessBuilder?
        var decoderBuilder: ProcessBuilder?

        try {
            var gap: ByteArray? = null

            for (file in parameters.audioFiles) {
                val decoderParams = Parameters.createDecoder(file)
                var parseHeader   = true
                var currProp      = Wav()

                Swing.logMessage = decoderParams.joinToString(separator = " ")

                decoderBuilder = ProcessBuilder(decoderParams)
                decoderProcess = decoderBuilder.start()
                inputStream    = decoderProcess.inputStream
                message        = "decoding '${file.name}' to '${parameters.outputFile.name}'"

                while (decoderProcess.isAlive == true) {
                    val read = inputStream.read(buffer)

                    if (read > 0) {
                        if (encoderProcess == null) {
                            firstProp        = Wav(buffer, read)
                            currProp         = firstProp
                            encoderParams    = Encoders.createEncoder(parameters, firstProp)
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
                            currProp = Wav(buffer, read)
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
            res2 = e.message.toString()
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
                res = "error: encoder failed, exit code=${encoderProcess.exitValue()}"
            }

            if (res == "" && decoderProcess != null && decoderProcess.exitValue() != 0) {
                res = "error: decoder failed, exit code=${decoderProcess.exitValue()}"
            }

            if (res.isNotEmpty() == true) {
                throw Exception(res)
            }
            else if (res2.isNotEmpty() == true) {
                throw Exception(res2)
            }
        }
    }
}
