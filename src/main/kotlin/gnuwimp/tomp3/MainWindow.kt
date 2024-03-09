/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.swing.*
import gnuwimp.util.*
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.StandardArtwork
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.util.prefs.Preferences
import javax.swing.*
import kotlin.system.exitProcess

//------------------------------------------------------------------------------
class MainWindow : JFrame(APP_NAME) {
    companion object {
        const val APP_NAME = "toMP3"
        const val ABOUT_APP = "About toMP3"
        val ABOUT_TEXT: String = "<html>" +
                "<h2>toMP3 2.3</h2>" +
                "<b>Copyright 2016 - 2021 gnuwimp@gmail.com.</b><br>" +
                "Released under the GNU General Public License v3.0.<br>" +
                "See <a href=\"https://github.com/gnuwimp/toMP3\">https://github.com/gnuwimp/toMP3</a>.<br>" +
                "Use toMP3 with caution and at your own risk.<br>" +
                "<br>" +
                "<b>About</b><br>" +
                "toMP3</b> is an audio file converter written in Kotlin.<br>" +
                "It started out as an audio book converter but can now also convert music and video files.<br>" +
                "It takes a number of input files and converts them into one single mp3 or ogg file.<br>" +
                "<br>" +
                "<b>Requirements</b><br>" +
                "Lame is used for encoding and decoding mp3 files.<br>" +
                "And oggenc for encoding ogg files.<br>" +
                "To decode aac/flac/wav/ogg/avi/mp4/mkv files ffmpeg must be installed.<br>" +
                "<br>" +
                "Download lame from <a href=\"https://lame.sourceforge.net\">https://lame.sourceforge.net</a>.<br>" +
                "Download oggenc from <a href=\"https://www.xiph.org/ogg\">https://www.xiph.org/ogg</a>.<br>" +
                "Download ffmpeg from <a href=\"https://www.ffmpeg.org\">https://www.ffmpeg.org</a>.<br>" +
                "<br>" +
                "<b>Usage</b><br>" +
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
                "<b>Command Line Arguments</b><br>" +
                "toMP3.jar can be run from the command line to do the encoding automatically and quit when it has finished.<br>" +
                "Use only ascii characters on Windows.<br>" +
                "Wrap strings that contain spaces with double quotes (\"/my path/to files\").<br>" +
                "<pre>" +
                "--src  [PATH]              source directory with audio files\n" +
                "--dest [PATH]              destination directory for target file\n" +
                "--artist [TEXT]            artist name\n" +
                "--title [TEXT]             album and title name\n" +
                "--comment [TEXT]           comment string (optional)\n" +
                "--cover [PATH]             track cover image (optional)\n" +
                "--year [YYYY]              track year (optional, 1 - 2100)\n" +
                "--genre [TEXT]             genre string (optional, default ${Parameters.DEFAULT_GENRE})\n" +
                "--gap [SECONDS]            insert silence between tracks (optional, default 0)\n" +
                "                             valid values are: 0 - 5\n" +
                "--mono                     downmix stereo to mono (optional)\n" +
                "--encoder                  index in encoder list (optional, default ${Encoders.DEFAULT.ordinal} -> MP3 CBR 128 Kbps)\n" +
                "${Encoders.toHelp}" +
                "--auto                     start automatically and quit after successful encoding (optional)\n" +
                "--auto2                    start automatically and quit even for error (optional)\n" +
                "</pre>" +
                "</html>"
    }

    private val pref: Preferences = Preferences.userNodeForPackage(Main.javaClass)
    private var sourceDir         = File(System.getProperty("user.home"))
    private var destDir           = File(System.getProperty("user.home"))
    private var imageDir          = File(System.getProperty("user.home"))
    private val main              = LayoutPanel(size = Swing.defFont.size / 2)
    private val sourceLabel       = JLabel("Source:")
    private val sourceInput       = JTextField()
    private val sourceButton      = JButton("Browse")
    private val destLabel         = JLabel("Destination:")
    private val destInput         = JTextField()
    private val destButton        = JButton("Browse")
    private val imageLabel        = JLabel("Cover Image:")
    private val imageInput        = JTextField()
    private val imageButton       = JButton("Browse")
    private val authorLabel       = JLabel("Artist")
    private val authorInput       = JTextField()
    private val titleLabel        = JLabel("Title:")
    private val titleInput        = JTextField()
    private val commentLabel      = JLabel("Comment:")
    private val commentInput      = JTextField()
    private val yearLabel         = JLabel("Year:")
    private val yearInput         = JTextField()
    private val genreLabel        = JLabel("Genre:")
    private val genreInput        = JTextField()
    private val encoderLabel      = JLabel("Encoder:")
    private val encoderCombo      = ComboBox<String>(strings = Encoders.toNames, Encoders.DEFAULT.encoderIndex);
    private val gapLabel          = JLabel("Gap:")
    private val gapCombo          = ComboBox<String>(strings = listOf("0", "1", "2", "3", "4", "5"), 0)
    private val channelLabel      = JLabel("Channels:")
    private val channelGroup      = ButtonGroup()
    private val channelMono       = JRadioButton("Mono")
    private val channelStereo     = JRadioButton("Stereo")
    private val aboutButton       = JButton("About")
    private val logButton         = JButton("Show Log")
    private val convertButton     = JButton("Convert")
    private val quitButton        = JButton("Quit")
    var auto                      = 0

    //--------------------------------------------------------------------------
    init {
        iconImage   = Main.icon
        contentPane = main

        val w = 16
        var y = 1

        main.add(sourceLabel, x = 1, y = y, w = w, h = 4)
        main.add(sourceInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(sourceButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(destLabel, x = 1, y = y, w = w, h = 4)
        main.add(destInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(destButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(imageLabel, x = 1, y = y, w = w, h = 4)
        main.add(imageInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(imageButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(authorLabel, x = 1, y = y, w = w, h = 4)
        main.add(authorInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(aboutButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(titleLabel, x = 1, y = y, w = w, h = 4)
        main.add(titleInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(logButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(commentLabel, x = 1, y = y, w = w, h = 4)
        main.add(commentInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(convertButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(yearLabel, x = 1, y = y, w = w, h = 4)
        main.add(yearInput, x = w + 2, y = y, w = 30, h = 4)

        y += 5
        main.add(genreLabel, x = 1, y = y, w = w, h = 4)
        main.add(genreInput, x = w + 2, y = y, w = 30, h = 4)

        y += 5
        main.add(gapLabel, x = 1, y = y, w = w, h = 4)
        main.add(gapCombo, x = w + 2, y = y, w = 30, h = 4)

        y += 5
        main.add(encoderLabel, x = 1, y = y, w = w, h = 4)
        main.add(encoderCombo, x = w + 2, y = y, w = 30, h = 4)

        y += 5
        main.add(channelLabel, x = 1, y = y, w = w, h = 4)
        main.add(channelMono, x = w + 2, y = y, w = 15, h = 4)
        main.add(channelStereo, x = w + 17, y = y, w = 15, h = 4)

        main.add(quitButton, x = -20, y = -5, w = -1, h = 4)

        channelGroup.add(channelMono)
        channelGroup.add(channelStereo)
        channelStereo.isSelected = true

        pack()

        sourceInput.toolTipText   = "Select source directory with all audio files."
        destInput.toolTipText     = "Select destination directory for the result file."
        imageInput.toolTipText    = "Select cover image file (optional)."
        authorInput.toolTipText   = "Set artist/author name."
        commentInput.toolTipText  = "Set comment string (optional)."
        titleInput.toolTipText    = "Set title/artist name."
        yearInput.toolTipText     = "Set year for the audio book (optional, 1 - 2100)."
        genreInput.toolTipText    = "Set track genre (optional)."
        encoderCombo.toolTipText  = "Select encoder for the result file."
        gapCombo.toolTipText      = "Insert silence between tracks (0 - 5 seconds)."
        channelMono.toolTipText   = "Convert stereo tracks to mono."
        channelStereo.toolTipText = "Keep tracks as they are, stereo or mono."

        sourceButton.toolTipText  = "Select source directory with all audio files."
        destButton.toolTipText    = "Select destination directory for the result file."
        imageButton.toolTipText   = "Select cover image file (optional)."
        logButton.toolTipText     = "Show log window with all tasks that have been executed."
        convertButton.toolTipText = "Start transcoding."

        //----------------------------------------------------------------------
        addWindowListener( object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                quit()
            }
        })

        //----------------------------------------------------------------------
        aboutButton.addActionListener {
            AboutHandler(appName = ABOUT_APP, aboutText = ABOUT_TEXT).show(parent = this)
        }

        //----------------------------------------------------------------------
        convertButton.addActionListener {
            run()
        }

        //----------------------------------------------------------------------
        destButton.addActionListener {
            val dialog = JFileChooser(destDir)

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.fontForAll        = Swing.defFont

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory) {
                destDir = dialog.selectedFile
                destInput.text = dialog.selectedFile.canonicalPath
                pref.destPath = destDir.canonicalPath
            }
        }

        //----------------------------------------------------------------------
        imageButton.addActionListener {
            val dialog = ImageFileDialog(imageDir.canonicalPath, this)
            val file   = dialog.file

            if (file != null && file.isImage) {
                imageDir        = file.parentFile
                imageInput.text = file.canonicalPath
                pref.imagePath  = file.parentFile.canonicalPath
            }
            else {
                imageInput.text = ""
            }
        }

        //----------------------------------------------------------------------
        logButton.addActionListener {
            val dialog = TextDialog(text = Swing.logMessage, showLastLine = true, title = "Log", parent = this)

            dialog.isVisible = true
        }

        //----------------------------------------------------------------------
        sourceButton.addActionListener {
            val dialog = JFileChooser(sourceDir)

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.fontForAll        = Swing.defFont

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory) {
                sourceDir        = dialog.selectedFile
                sourceInput.text = dialog.selectedFile.canonicalPath
                pref.sourcePath  = sourceDir.canonicalPath
            }
        }

        //----------------------------------------------------------------------
        quitButton.addActionListener {
            quit()
        }
    }

    //--------------------------------------------------------------------------
    fun argLoad(args: Array<String>): Boolean {
        try {
            val source  = args.findString("--src", "")
            val dest    = args.findString("--dest", "")
            val cover   = args.findString("--cover", "")
            val author  = args.findString("--author", "")
            val artist  = args.findString("--artist", "")
            val title   = args.findString("--title", "")
            val comment = args.findString("--comment", "")
            val year    = args.findString("--year", "")
            val genre   = args.findString("--genre", Parameters.DEFAULT_GENRE)
            var gap     = args.findInt("--gap", 0).toInt()
            val mono    = args.find("--mono") != -1
            val encoder = args.findInt("--encoder", Encoders.DEFAULT.encoderIndex.toLong()).toInt()

            if (args.find("--auto2") != -1) {
                auto = 2
            }
            else if (args.find("--auto") != -1) {
                auto = 1
            }

            if (source != "") {
                sourceInput.text = source
            }

            if (dest != "") {
                destInput.text = dest
            }

            if (cover != "") {
                imageInput.text = cover
            }

            if (author != "") {
                authorInput.text = author
            }
            else if (artist != "") {
                authorInput.text = artist
            }

            if (title != "") {
                titleInput.text = title
            }

            if (comment != "") {
                commentInput.text = comment
            }

            if (year != "") {
                yearInput.text = year
            }

            if (genre != "") {
                genreInput.text = genre
            }

            if (mono == true) {
                channelMono.isSelected = true
            }

            encoderCombo.selectedIndex = Encoders.toEncoder(encoder).encoderIndex

            if (gap < 0 || gap >= gapCombo.itemCount) {
                throw Exception("error: invalid value for --gap ($gap)")
            }

            return true
        }
        catch (e: Exception) {
            if (auto == 2) {
                println(e.message)
                quit()
            }
            else {
                JOptionPane.showMessageDialog(null, e.message, APP_NAME, JOptionPane.ERROR_MESSAGE)
            }

            return false
        }
    }

    //--------------------------------------------------------------------------
    fun quit() {
        prefSave()
        isVisible = false
        dispose()
        exitProcess(status = 0)
    }

    //--------------------------------------------------------------------------
    private fun stage1SetParameters(): Parameters {
        val parameters = Parameters(
            source      = sourceInput.text,
            dest        = destInput.text,
            cover       = imageInput.text,
            artist      = authorInput.text,
            title       = titleInput.text,
            year        = yearInput.text,
            comment     = commentInput.text,
            genre       = genreInput.text,
            encoder     = Encoders.toEncoder(encoderCombo.selectedIndex),
            gap         = gapCombo.text,
            mono        = channelMono.isSelected,
        )

        parameters.validate()
        return parameters
    }

    //--------------------------------------------------------------------------
    private fun stage2LoadFiles(parameters: Parameters) {
        val files = File(parameters.source).listFiles()

        if (files != null) {
            parameters.audioFiles = files.filter { file ->
                file.isFile && (file.extension.lowercase() == "mp3" ||
                                file.extension.lowercase() == "aac" ||
                                file.extension.lowercase() == "flac" ||
                                file.extension.lowercase() == "ogg" ||
                                file.extension.lowercase() == "wav" ||
                                file.extension.lowercase() == "avi" ||
                                file.extension.lowercase() == "mkv" ||
                                file.extension.lowercase() == "mp4")
            }
        }

        if (parameters.audioFiles.isEmpty() == true) {
            throw Exception("error: no audio files in source directory")
        }

        parameters.audioFiles = parameters.audioFiles.sortedBy { it.path }
    }

    //--------------------------------------------------------------------------
    private fun stage3Transcoding(parameters: Parameters) {
        val tasks    = mutableListOf<Task>(TranscoderTask(parameters = parameters))
        val progress = TaskManager(tasks = tasks, threadCount = 1, onError = TaskManager.Execution.STOP_JOIN, onCancel = TaskManager.Execution.STOP_JOIN)
        val dialog   = TaskDialog(taskManager = progress, title = "Converting Files", type = TaskDialog.Type.PERCENT, parent = this)

        dialog.enableCancel = true
        dialog.start(updateTime = 50L)
        tasks.throwFirstError()
    }

    //--------------------------------------------------------------------------
    private fun stage4WriteTags(parameters: Parameters) {
        try {
            val track = AudioFileIO.read(parameters.outputFile)
            val tag   = track.tagOrCreateDefault

            tag.setField(FieldKey.ALBUM, parameters.title)
            tag.setField(FieldKey.ALBUM_ARTIST, parameters.artist)
            tag.setField(FieldKey.ARTIST, parameters.artist)
            tag.setField(FieldKey.COMMENT, parameters.comment)
            tag.setField(FieldKey.ENCODER, parameters.encoder.encoderExe)
            tag.setField(FieldKey.GENRE, parameters.genre)
            tag.setField(FieldKey.TITLE, parameters.title)
            tag.setField(FieldKey.TRACK, "1")
            tag.setField(FieldKey.TRACK_TOTAL, "1")

            if (parameters.year.numOrMinus >= 0) {
                tag.setField(FieldKey.YEAR, parameters.year)
            }

            if (parameters.cover.isNotBlank() == true) {
                tag.addField(StandardArtwork.createArtworkFromFile(File(parameters.cover)))
            }

            track.tag = tag
            track.commit()
        }
        catch (e: Exception) {
            throw Exception("error: failed to write tags\n${e.message.toString()}")
        }
    }

    //--------------------------------------------------------------------------
    fun prefLoad() {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        fontForAll            = Swing.defFont

        val w  = pref.winWidth
        val h  = pref.winHeight
        var x  = pref.winX
        var y  = pref.winY
        val sc = Toolkit.getDefaultToolkit().screenSize

        if (x > sc.getWidth() || x < -50) {
            x = 0
        }

        if (y > sc.getHeight() || y < -50) {
            y = 0
        }

        setLocation(x, y)
        setSize(w, h)

        if (pref.winMax == true) {
            extendedState = MAXIMIZED_BOTH
        }

        destDir   = File(pref.destPath)
        sourceDir = File(pref.sourcePath)
        imageDir  = File(pref.imagePath)
    }

    //--------------------------------------------------------------------------
    private fun prefSave() {
        try {
            pref.winWidth  = size.width
            pref.winHeight = size.height
            pref.winX      = location.x
            pref.winY      = location.y
            pref.winMax    = (extendedState and MAXIMIZED_BOTH != 0)

            pref.flush()
        }
        catch (e: Exception) {
        }
    }

    //--------------------------------------------------------------------------
    fun run() {
        var file = File("")

        try {
            val parameters = stage1SetParameters()
            file = parameters.outputFile
            stage2LoadFiles(parameters)
            stage3Transcoding(parameters)
            file = File("")
            stage4WriteTags(parameters)

            Swing.logMessage = "encoding finished successfully with file '${parameters.outputFile.name}'"

            if (auto != 0) {
                quit()
            }
            else {
                JOptionPane.showMessageDialog(this, "encoding finished successfully with file '${parameters.outputFile.name}'", APP_NAME, JOptionPane.INFORMATION_MESSAGE)
            }
        }
        catch (e: Exception) {
            if (file.isFile == true) {
                file.remove()
            }

            if (auto == 2) {
                println("${e.message}")
                quit()
            }
            else {
                Swing.logMessage = e.message ?: "!"
                JOptionPane.showMessageDialog(this, e.message, APP_NAME, JOptionPane.ERROR_MESSAGE)
            }
        }
        finally {
            System.gc()
        }
    }
}
