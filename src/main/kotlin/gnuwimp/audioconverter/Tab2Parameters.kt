/*
 * Copyright © 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo
import java.io.File

//------------------------------------------------------------------------------
class Tab2Parameters(val source: String = "", val dest: String = "", val encoder: Encoders = Encoders.MP3_CBR_320, val threads: Int = 1) {
    var inputFiles: List<FileInfo> = listOf()
    var outputFiles: MutableList<FileInfo> = mutableListOf()

    //--------------------------------------------------------------------------
    fun validate() {
        val s = FileInfo(source).canonicalPath + "/"
        val d = FileInfo(dest).canonicalPath + "/"

        when {
            File(source).isDirectory == false -> throw Exception("error: missing source directory => '$source'")
            File(dest).isDirectory == false -> throw Exception("error: missing destination directory => '$dest'")
            FileInfo(source).isCircular == true -> throw Exception("error: start directory han an circular link")
            FileInfo(dest).isCircular == true -> throw Exception("error: destination directory han an circular link")
            s.indexOf(d) == 0 -> throw Exception("error: keep source and destination directories separate")
            d.indexOf(s) == 0 -> throw Exception("error: keep source and destination directories separate")
        }
    }

    //--------------------------------------------------------------------------
    override fun toString(): String = "start=$source, dest=$dest, enocder=$encoder"
}
