/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.util.isImage
import gnuwimp.util.numOrZero
import java.io.File

//------------------------------------------------------------------------------
class Parameters(val source: String = "", val dest: String = "", val cover: String = "", val artist: String = "", val title: String = "", val year: String = "", val comment: String = "", val genre: String = "", val encoder: Encoders = Encoders.MP3_CBR_128, val gap: String = "", val mono: Boolean = false) {
    var audioFiles: List<File> = listOf()

    companion object {
        const val DEFAULT_GENRE = "Audiobook"

        //--------------------------------------------------------------------------
        fun createDecoder(file: File): List<String> {
            return if (file.extension.lowercase() == "mp3") {
                listOf<String>("lame", "--quiet", "--decode", file.path, "-")
            }
            else {
                listOf<String>("ffmpeg", "-loglevel", "level+quiet", "-i", file.path, "-f", "wav", "-")
            }
        }
    }
    //--------------------------------------------------------------------------
    val outputFile: File
        get() {
            return if (year != "") {
                File("$dest${File.separator}$artist - $title ($year).${encoder.fileExt}")
            }
            else {
                File("$dest${File.separator}$artist - $title.${encoder.fileExt}")
            }
        }

    //--------------------------------------------------------------------------
    fun validate() {
        when {
            File(source).isDirectory == false -> throw Exception("error: missing source directory")
            File(dest).isDirectory == false -> throw Exception("error: missing destination directory")
            cover.isNotBlank() && File(cover).isImage == false -> throw Exception("error: image cover file is not an valid image")
            artist.isBlank() -> throw Exception("error: artist/author string is empty")
            title.isBlank() -> throw Exception("error: title string is empty")
            year != "" && (year.numOrZero < 1 || year.numOrZero > 2100) -> throw Exception("error: year is out of range $year (1 - 2100)")
            outputFile.isFile == true -> throw Exception("error: destination file '${outputFile.absolutePath}' exist!")
        }
    }

    //--------------------------------------------------------------------------
    override fun toString(): String = "source=$source, dest=$dest, cover=$cover, artist=$artist, title=$title, year=$year, comment=$comment, enocder=$encoder"
}
