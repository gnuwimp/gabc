/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.swing.Swing
import gnuwimp.util.*
import java.io.InputStream
import java.io.OutputStream

//------------------------------------------------------------------------------
class Tab1Task(val parameters: Tab1Parameters) : Task(max = parameters.audioFiles.sumByLong(FileInfo::size)) {
    //--------------------------------------------------------------------------
    override fun run() {
        var decoderBuilder: ProcessBuilder? = null
        var decoderProcess: Process?        = null
        var decoderStream: InputStream?     = null
        var encoderBuilder: ProcessBuilder? = null
        var encoderProcess: Process?        = null
        var encoderStream: OutputStream?    = null
        var wavHeader                       = WavHeader()
        var exception                       = ""

        try {
            var gap: ByteArray? = null

            for (file in parameters.audioFiles) {
                val buffer        = ByteArray(size = 131_072)
                val decoderParams = Decoder.create(file, parameters.mono)
                var parseHeader   = true
                message           = "${file.filename}\n${parameters.outputFileName}"

                Swing.logMessage = decoderParams.joinToString(separator = " ")

                decoderBuilder = ProcessBuilder(decoderParams)
                decoderProcess = decoderBuilder.start()
                decoderStream  = decoderProcess.inputStream

                while (decoderProcess.isAlive == true) {
                    val read = decoderStream.read(buffer)

                    if (read > 0) {
                        ConvertManager.add(read.toLong())

                        if (encoderProcess == null) {
                            wavHeader         = WavHeader(buffer, read)
                            val encoderParams = Encoders.createEncoder(parameters, wavHeader)
                            Swing.logMessage  = encoderParams.joinToString(separator = " ")
                            encoderBuilder    = ProcessBuilder(encoderParams)
                            encoderProcess    = encoderBuilder.start()
                            encoderStream     = encoderProcess.outputStream
                            parseHeader       = false
                            val seconds       = parameters.gap.numOrZero.toInt()

                            if (seconds != 0) {
                                gap = ByteArray(size = (wavHeader.sampleRate * wavHeader.channels.ordinal * 2 * seconds))
                            }

                            encoderStream?.write(buffer, wavHeader.data, read - wavHeader.data)
                        }
                        else if (parseHeader == true) {
                            val currentHeader = WavHeader(buffer, read)

                            if (currentHeader.sampleRate != wavHeader.sampleRate || currentHeader.channels != wavHeader.channels || currentHeader.bitWidth != wavHeader.bitWidth) {
                                throw Exception("error: channels or samplerate or bitwidth are different for these tracks\n${parameters.audioFiles[0].name} (${wavHeader.sampleRateString} Khz, ${wavHeader.channelString}, ${wavHeader.bitWidth} bit)\n${file.name} (${currentHeader.sampleRateString} Khz, ${currentHeader.channelString}, ${currentHeader.bitWidth} bit)")
                            }

                            encoderStream?.write(buffer, currentHeader.data, read - currentHeader.data)
                            parseHeader = false
                        }
                        else {
                            encoderStream?.write(buffer, 0, read)
                        }
                    }

                    if (abort == true) {
                        throw Exception(Constants.CANCEL_ERROR)
                    }
                }

                if (gap != null && file != parameters.audioFiles.last()) {
                    encoderStream?.write(gap, 0, gap.size)
                }

                progress += file.size

                decoderStream.safeClose()
                decoderProcess.waitFor()

                if (decoderProcess.exitValue() != 0) {
                    throw Exception("")
                }

                decoderStream  = null
                decoderProcess = null
            }
        }
        catch (e: Exception) {
            exception = e.message.toString()
        }
        finally {
            decoderStream?.safeClose()
            encoderStream?.safeClose()

            if (decoderProcess?.isAlive == true) {
                decoderProcess.waitFor()
            }

            if (encoderProcess?.isAlive == true) {
                encoderProcess.waitFor()
            }

            if (encoderProcess != null && encoderProcess.exitValue() != 0) {
                exception = "error: encoder failed, exit code=${encoderProcess.exitValue()} - $exception"
            }
            else if (decoderProcess != null && decoderProcess.exitValue() != 0) {
                exception = "error: decoder failed, exit code=${decoderProcess.exitValue()} - $exception"
            }

            if (exception != "") {
                throw Exception(exception)
            }
        }
    }
}
