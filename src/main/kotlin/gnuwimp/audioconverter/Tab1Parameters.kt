/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.FileInfo
import gnuwimp.util.isImage
import gnuwimp.util.numOrZero
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.Artwork
import java.io.File

//------------------------------------------------------------------------------
class Tab1Parameters(val audioFiles: List<FileInfo>, val source: String, val dest: String, val cover: String, var artist: String, var album: String, var year: String, val comment: String, var genre: String, val encoder: Encoders, val gap: String, val mono: Boolean, val overwrite: Constants.Overwrite) {
    var image: Artwork? = null

    companion object {
        const val DEFAULT_GENRE = "Audiobook"
    }

    //--------------------------------------------------------------------------
    val outputFile: File
        get() = File(outputFileName)

    //--------------------------------------------------------------------------
    val outputFileName: String
        get() {
            val name = FileInfo.safeName("$artist - $album")
            val tmp  = if (dest.endsWith(File.separator) == true) dest else dest + File.separator

            return if (year != "") {
                return "$tmp$name ($year).${encoder.fileExt}"
            }
            else {
                return "$tmp$name.${encoder.fileExt}"
            }
        }

    //--------------------------------------------------------------------------
    fun validate() {
        val d = FileInfo(dest)

        if (d.isDir == false && d.file.mkdirs() == false) {
            throw Exception("error: missing destination directory => '$dest'")
        }

        try {
            val file0 = AudioFileIO.read(audioFiles[0].file)
            val tag0  = file0.tag

            if (tag0 != null) {
                if (artist.isBlank() == true) {
                    artist = tag0.getFirst(FieldKey.ARTIST)
                }

                if (album.isBlank() == true) {
                    album = tag0.getFirst(FieldKey.ALBUM)
                }

                if (genre.isBlank() == true) {
                    genre = tag0.getFirst(FieldKey.GENRE)
                }

                if (year.isBlank() == true) {
                    year = tag0.getFirst(FieldKey.YEAR)

                    if (year.length > 4) {
                        year = year.substring(0, 4)
                    }
                }

                image = tag0.firstArtwork
            }
        }
        catch (e: Exception) {
        }

        when {
            cover.isNotBlank() && File(cover).isImage == false -> throw Exception("error: image cover file is not an valid image")
            artist.isBlank() -> throw Exception("error: artist/author string is empty")
            album.isBlank() -> throw Exception("error: title string is empty")
            year != "" && (year.numOrZero < 1900 || year.numOrZero > 2100) -> throw Exception("error: year is out of range $year (1900 - 2100)")
        }

        val out_file = FileInfo(outputFileName)

        if (overwrite == Constants.Overwrite.NO && out_file.isFile == true) {
            throw Exception("error: destination file '${out_file.filename}' exist!")
        }


        if (overwrite == Constants.Overwrite.OLDER) {
            var count = 0

            for (file in audioFiles) {
                val in_file  = FileInfo(file.filename)
                count += if (out_file.mod < in_file.mod) 1 else 0

            }

            if (count == 0) {
                throw FileExistException("error: destination file '${out_file.filename}' is newer than the input files!")
            }
        }

        Main.pref.tab1SourcePath = source
        Main.pref.tab1DestPath   = dest
        Main.pref.tab1ImagePath  = cover
    }

    //--------------------------------------------------------------------------
    override fun toString(): String = "source=$source, dest=$dest, cover=$cover, artist=$artist, title=$album, year=$year, comment=$comment, enocder=$encoder"
}
