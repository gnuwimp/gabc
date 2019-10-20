/*
 * Copyright 2016 - 2019 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.gabc

import gnuwimp.core.extension.isImage
import gnuwimp.core.extension.numOrZero
import java.io.File

/**
 * Create parameter for the transcoding job
 */
class Parameters(val source: String = "", val dest: String = "", val cover: String = "", val author: String = "", val title: String = "", val year: String = "", val comment: String = "", val bitrate: String = "") {
    var mp3Files: List<File> = listOf()
    val mp3                  = File("$dest${File.separator}$author - $title ($year).mp3")

    /**
     * Check data and if error throw exception
     */
    fun check() {
        when {
            !File(source).isDirectory -> throw Exception("error: missing source directory")
            !File(dest).isDirectory -> throw Exception("error: missing destination directory")
            cover.isNotBlank() && !File(cover).isImage -> throw Exception("error: image cover file is not an valid image")
            author.isBlank() -> throw Exception("error: author string is empty")
            title.isBlank() -> throw Exception("error: title string is empty")
            year.numOrZero < 1 || year.numOrZero > 9999 -> throw Exception("error: year is out of range")
            bitrate.numOrZero < 24 || bitrate.numOrZero > 320 -> throw Exception("error: bitrate us out of range")
        }
    }

    /**
     * Debug string
     */
    override fun toString(): String = "source=$source, dest=$dest, cover=$cover, author=$author, title=$title, year=$year, comment=$comment, bitrate=$bitrate"
}
