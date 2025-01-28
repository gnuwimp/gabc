/*
 * Copyright 2021 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

object Constants {
    const val APP_NAME                   = "AudioConverter"
    const val ABOUT_APP                  = "About AudioConverter"
    const val CANCEL_ERROR               = "user abort!"
    const val LOGBUTTON_TOOLTIP          = "Show log window with all tasks that have been executed."
    const val ENCODERCOMBO_TOOLTIP       = "Select encoder."
    const val CONVERTBUTTON_TOOLTIP      = "Start converting all files."
    const val HELP                       = "Help"

    const val TAB1_LABEL                 = "Convert input files into one file"
    const val TAB1_TOOLTIP               = "Convert a number of input tracks into one single mp3 or ogg file."
    const val TAB1_SOURCEINPUT_TOOLTIP   = "Select source directory with all audio/video files."
    const val TAB1_DESTINPUT_TOOLTIP     = "Select destination directory for the result file."
    const val TAB1_IMAGEINPUT_TOOLTIP    = "Select cover image file (optional)."
    const val TAB1_AUTHORINPUT_TOOLTIP   = "Set artist/author name."
    const val TAB1_COMMENTINPUT_TOOLTIP  = "Set comment string (optional)."
    const val TAB1_TITLEINPUT_TOOLTIP    = "Set title/artist name."
    const val TAB1_YEARINPUT_TOOLTIP     = "Set year for the audio book (optional, 1900 - 2100)."
    const val TAB1_GENREINPUT_TOOLTIP    = "Set track genre (optional)."
    const val TAB1_GAPCOMBO_TOOLTIP      = "Insert silence between tracks (0 - 5 seconds)."
    const val TAB1_CHANNELMONO_TOOLTIP   = "Convert stereo tracks to mono."
    const val TAB1_CHANNELSTEREO_TOOLTIP = "Keep tracks as they are, stereo or mono."

    const val TAB2_LABEL                 = "Convert files one by one"
    const val TAB2_TOOLTIP               = "<html>Convert files to mp3 or ogg.<br>For best audio quality do use lossless files formats.<br>Such as wav and flac.</html>"
    const val TAB2_STARTINPUT_TOOLTIP    = "Select start directory with all audio files."
    const val TAB2_DESTINPUT_TOOLTIP     = "Select destination directory."
    const val TAB2_THREADS_TOOLTIP       = "Set number of threads to use when converting files."
    val       TAB2_THREADS               = listOf("1", "2", "3", "4", "5", "6", "7", "8", "12", "16", "24", "32", "48", "64", "96", "128")
    val       TAB2_OVERWRITE             = listOf("0", "1", "2")

    enum class Auto {
        NO,
        YES_STOP_ON_ERROR,
        YES_QUIT_ON_ERROR
    }

    enum class Channels {
        INVALID,
        MONO,
        STEREO,
    }

    enum class Overwrite {
        NO,
        OLDER,
        ALL
    }

    val TAB1_HELP_TEXT: String = "<html>" +
            "<h2>Convert a directory with files into one mp3/ogg file</h2>" +

            "<h3>Usage</h3>" +
            "Select a source directory with audio or video files.<br>" +
            "All audio/video files in input directory will be converted to one mp3/ogg file.<br>" +
            "They must have names that make them sorted in playing order.<br>" +
            "And all input files must have same audio properties (<i>mono/stereo/samplerate/bitwidth</i>).<br>" +
            "<br>" +

            "Then select one of the encoders in the list.<br>" +
            "Use mono option to force stereo tracks to be converted to mono.<br>" +
            "<br>" +

            "The finished audio file will end up in the destination directory.<br>" +
            "With <i>artist - title (year).mp3/ogg</i> or <i>artist - title.mp3/ogg</i> as file name<br>" +
            "<br>" +

            "<h3>Command line arguments</h3>" +
            "Probably best to use only ascii characters on Windows.<br>" +
            "Wrap strings that contain spaces with double quotes (\"/my path/to files\").<br>" +
            "<pre>" +
            "--src  [PATH]              source directory with audio files\n" +
            "--dest [PATH]              destination directory for target file\n" +
            "--artist [TEXT]            artist name\n" +
            "--title [TEXT]             album and title name\n" +
            "--comment [TEXT]           comment string (optional)\n" +
            "--cover [PATH]             track cover image (optional)\n" +
            "--year [YYYY]              track year (optional, 1900 - 2100)\n" +
            "--genre [TEXT]             genre string (optional, default ${Tab1Parameters.DEFAULT_GENRE})\n" +
            "--gap [SECONDS]            insert silence between tracks (optional, default 0)\n" +
            "                             valid values are: 0 - 5\n" +
            "--mono                     downmix stereo to mono (optional)\n" +
            "--encoder [INDEX]          index in encoder list (optional, default ${Encoders.DEFAULT.ordinal} -> MP3 CBR 128 Kbps)\n" +
            "--overwrite [VALUE]        overwrite destination files (optional, default 0)\n" +
            "                             valid values are: 0 dont overwrite, 1 overwrite older, 3 overwrite all\n" +
            Encoders.toHelp +
            "--auto                     start automatically and quit after successful encoding (optional)\n" +
            "--auto2                    start automatically and quit even for error (optional)\n" +
            "<br><br><br>" +
            "</pre>" +
            "</html>"

    val TAB2_HELP_TEXT: String = "<html>" +
            "<h2>Convert all files in a directory tree to mp3/ogg</h2>" +

            "<h3>Usage</h3>" +
            "Select start directory.<br>" +
            "Select destination directory (must be outside of start directory).<br>" +
            "Select encoder.<br>" +
            "Set number of threads.<br>" +
            "Then convert.<br>" +
            "<br>" +

            "It will stop for any decoding/encoding error.<br>" +
            "But tag writing errors will be logged only.<br>" +
            "<br>" +

            "All destination files that already exist will be excluded.<br>" +
            "So you can stop the encoding and restart at a later point and it will continue where you left off.<br>" +
            "The most common tags (if possible) will be copied including cover art.<br>" +
            "<br>" +

            "<h3>Command line arguments</h3>" +
            "<pre>" +
            "--mode2                    set this mode\n" +
            "--src [PATH]               root directory with audio files\n" +
            "--dest [PATH]              destination directory for target file\n" +
            "--threads [COUNT]          set number of threads to use (optional, default 1)\n" +
            "                             valid values are: 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32, 48, 64, 96, 128\n" +
            "--encoder [INDEX]          index in encoder list (optional, default ${Encoders.DEFAULT.ordinal} -> MP3 CBR 128 Kbps)\n" +
            "--overwrite [VALUE]        overwrite destination files (optional, default 0)\n" +
            "                             valid values are: 0 dont overwrite, 1 overwrite older, 3 overwrite all\n" +
            Encoders.toHelp +
            "--auto                     start automatically and quit after successful encoding (optional)\n" +
            "--auto2                    start automatically and quit even for error (optional)\n" +
            "<br><br><br>" +
            "</pre>" +
            "</html>"

    //----------------------------------------------------------------------
    fun aboutApp(): String {
        var about = "<html>" +

        "<h2>AudioConverter 2.6.1</h2>" +

        "Copyright 2021 - 2025 gnuwimp@gmail.com.<br>" +
        "Released under the GNU General Public License v3.0.<br>" +
        "See <a href=\"https://github.com/gnuwimp/AudioConverter\">https://github.com/gnuwimp/AudioConverter</a>.<br>" +
        "Use AudioConverter with caution and at your own risk.<br>" +
        "<br>" +

        "<h3>About</h3>" +
        "This program converts audio and video files to mp3/ogg/aac audio files.<br>" +
        "Either all input files in one directory into one file.<br>" +
        "Or file by file for all files that are found in a directory tree.<br>" +
        "<br>" +

        "<h3>Requirements</h3>" +
        "Lame is used for encoding mp3 files.<br>" +
        "Oggenc for encoding ogg files.<br>" +
        "Qaac64 for encoding aac files (by using iTunes on windows).<br>" +
        "Ffmpeg for decoding mp3/flac/wav/ogg/m4a/aac/mp4/avi/mkv files.<br>" +
        "<br>" +

        "Download lame from <a href=\"https://lame.sourceforge.net\">https://lame.sourceforge.net</a>.<br>" +
        "Download oggenc from <a href=\"https://www.xiph.org/ogg\">https://www.xiph.org/ogg</a>.<br>" +
        "Download qaac64 from <a href=\"https://github.com/nu774/qaac\">https://github.com/nu774/qaac</a>.<br>" +
        "Download ffmpeg from <a href=\"https://www.ffmpeg.org\">https://www.ffmpeg.org</a>.<br>" +
        "<br>" +

        "<h3>Following third party software library are used</h3>" +
        "JAudioTagger - <a href=\"http://www.jthink.net/jaudiotagger\">http://www.jthink.net/jaudiotagger</a><br>" +

        "<br>" +
        "<h3>Versions</h3>"

        about += "Java: " + System.getProperty("java.version") + "<br>"
        about += "Kotlin: " + KotlinVersion.CURRENT + "<br>"
        about += "JAudioTagger: 3.0.2-SNAPSHOT" + "<br><br><br>"
        about += "<html>"

        return about
    }
}
