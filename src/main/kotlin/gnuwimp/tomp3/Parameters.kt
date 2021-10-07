/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.util.isImage
import gnuwimp.util.numOrZero
import java.io.File

//------------------------------------------------------------------------------
class Parameters(val source: String = "", val dest: String = "", val cover: String = "", val artist: String = "", val title: String = "", val year: String = "", val comment: String = "", val genre: String = "", val bitrate: String = "", val gap: String = "", val mono: Boolean = false, val vbr: Boolean = false) {
    var audioFiles: List<File> = listOf()
    val mp3                    = if (year != "") File("$dest${File.separator}$artist - $title ($year).mp3") else File("$dest${File.separator}$artist - $title.mp3")

    //--------------------------------------------------------------------------
    fun validate() {
        when {
            File(source).isDirectory == false -> throw Exception("error: missing source directory")
            File(dest).isDirectory == false -> throw Exception("error: missing destination directory")
            cover.isNotBlank() && File(cover).isImage == false -> throw Exception("error: image cover file is not an valid image")
            artist.isBlank() -> throw Exception("error: artist/author string is empty")
            title.isBlank() -> throw Exception("error: title string is empty")
            year != "" && (year.numOrZero < 1 || year.numOrZero > 2100) -> throw Exception("error: year is out of range $year (1 - 2100)")
            bitrate.numOrZero < 24 || bitrate.numOrZero > 320 -> throw Exception("error: bitrate is out of range $bitrate (24 - 320)")
            gap.numOrZero < 0 || gap.numOrZero > 5 -> throw Exception("error: gap is out of range $bitrate (0 - 5)")
            mp3.isFile == true -> throw Exception("error: destination file '${mp3.absolutePath}' exist!")
        }
    }

    //--------------------------------------------------------------------------
    override fun toString(): String = "source=$source, dest=$dest, cover=$cover, author=$artist, title=$title, year=$year, comment=$comment, bitrate=$bitrate"
}
