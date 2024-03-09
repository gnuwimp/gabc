/*
 * Copyright © 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo

object Decoder {
    //--------------------------------------------------------------------------
    fun create(file: FileInfo): List<String> {
        return if (file.ext.lowercase() == "mp3") {
            listOf("lame", "--quiet", "--decode", file.filename, "-")
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
