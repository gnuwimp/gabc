/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo
import gnuwimp.util.isImage
import gnuwimp.util.numOrZero
import java.io.File

//------------------------------------------------------------------------------
class Tab1Parameters(val source: String, val dest: String, val cover: String, val artist: String, val title: String, val year: String, val comment: String, val genre: String, val encoder: Encoders, val gap: String, val mono: Boolean) {
    var audioFiles: List<FileInfo> = listOf()

    companion object {
        const val DEFAULT_GENRE = "Audiobook"
    }

    //--------------------------------------------------------------------------
    val outputFile: File
        get() {
            val name = FileInfo.safeName("$artist - $title")

            return if (year != "") {
                File("$dest${File.separator}$name ($year).${encoder.fileExt}")
            }
            else {
                File("$dest${File.separator}$name.${encoder.fileExt}")
            }
        }

    //--------------------------------------------------------------------------
    fun validate() {
        val d = FileInfo(dest)

        if (d.isDir == false && d.file.mkdirs() == false) {
            throw Exception("error: missing destination directory => '$dest'")
        }

        when {
            File(source).isDirectory == false -> throw Exception("error: missing source directory => '$source'")
            cover.isNotBlank() && File(cover).isImage == false -> throw Exception("error: image cover file is not an valid image")
            artist.isBlank() -> throw Exception("error: artist/author string is empty")
            title.isBlank() -> throw Exception("error: title string is empty")
            year != "" && (year.numOrZero < 1 || year.numOrZero > 2100) -> throw Exception("error: year is out of range $year (1 - 2100)")
            outputFile.isFile == true -> throw Exception("error: destination file '${outputFile.absolutePath}' exist!")
        }

        Main.pref.tab1SourcePath = source
        Main.pref.tab1DestPath   = dest
        Main.pref.tab1ImagePath  = cover
    }

    //--------------------------------------------------------------------------
    override fun toString(): String = "source=$source, dest=$dest, cover=$cover, artist=$artist, title=$title, year=$year, comment=$comment, enocder=$encoder"
}
