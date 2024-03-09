/*
 Copyright 2021 - 2023 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo

object Decoder {
    //--------------------------------------------------------------------------
    fun create(file: FileInfo, downmix: Boolean): List<String> {
        return if (downmix == true) {
            listOf("ffmpeg", "-loglevel", "level+quiet", "-i", file.filename, "-f", "wav", "-ac", "1", "-")
        }
        else {
            listOf("ffmpeg", "-loglevel", "level+quiet", "-i", file.filename, "-f", "wav", "-")
        }
    }
}

//--------------------------------------------------------------------------
val FileInfo.isAudioFile: Boolean
    get() {
        if (file.isFile == false) {
            return false
        }

        return when(ext.lowercase()) {
            "mp3"-> true
            "aac" -> true
            "m4a" -> true
            "m4b" -> true
            "flac" -> true
            "ogg" -> true
            "wav" -> true
            "avi" -> true
            "mkv" -> true
            "mp4" -> true
            else -> false
        }
    }
