/*
 Copyright 2021 - 2023 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo
import java.io.File

//------------------------------------------------------------------------------
class Tab2Parameters(val source: String, val dest: String, val encoder: Encoders, val threads: Int) {
    var inputFiles: List<FileInfo> = listOf()
    var outputFiles: MutableList<FileInfo> = mutableListOf()

    //--------------------------------------------------------------------------
    fun validate() {
        val s = FileInfo(source)
        val d = FileInfo(dest)
        val sc = s.canonicalPath + File.separator
        val dc = d.canonicalPath + File.separator

        if (d.isDir == false && d.file.mkdirs() == false) {
            throw Exception("error: missing destination directory => '$dest'")
        }

        when {
            s.isDir == false -> throw Exception("error: missing source directory => '$source'")
            s.isCircular == true -> throw Exception("error: start directory han an circular link")
            d.isCircular == true -> throw Exception("error: destination directory han an circular link")
            sc.indexOf(dc) == 0 -> throw Exception("error: keep source and destination directories separate")
            dc.indexOf(sc) == 0 -> throw Exception("error: keep source and destination directories separate")
        }

        Main.pref.tab2SourcePath = source
        Main.pref.tab2DestPath   = dest
    }

    //--------------------------------------------------------------------------
    override fun toString(): String = "start=$source, dest=$dest, enocder=$encoder"
}
