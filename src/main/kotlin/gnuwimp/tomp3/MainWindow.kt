/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.swing.*
import gnuwimp.util.*
import java.awt.Frame
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
        const val ABOUT_TEXT: String = "<html>" +
                "<h2>toMP3 2.2</h2>" +
                "<b>Copyright 2016 - 2021 gnuwimp@gmail.com.</b><br>" +
                "Released under the GNU General Public License v3.0.<br>" +
                "See <a href=\"https://github.com/gnuwimp/toMP3\">https://github.com/gnuwimp/toMP3</a>.<br>" +
                "</font>" +
                "<br>" +
                "<b>toMP3</b> is an audio file (book or music) converter written in Kotlin.<br>" +
                "Use toMP3 with caution and at your own risk.<br>" +
                "<br>" +
                "toMP3 converts one or more audio and video files into one mp3 file.<br>" +
                "It uses lame for encoding and decoding mp3 files.<br>" +
                "To decode aac/flac/wav/ogg/avi/mp4/mkv files ffmpeg must be installed.<br>" +
                "Download lame from <a href=\"https://lame.sourceforge.net\">https://lame.sourceforge.net</a>.<br>" +
                "And ffmpeg from <a href=\"https://www.ffmpeg.org\">https://www.ffmpeg.org</a>.<br>" +
                "<br>" +
                "Select a source directory with audio files.<br>" +
                "All audio files in that directory will be converted to one mp3 file.<br>" +
                "They must have names that make them sorted in playing order.<br>" +
                "And all input files must have same audio properties (<b>mono/stereo/samplerate/bitwidth</b>).<br>" +
                "<br>" +
                "The finished audio file will end up in the destination directory.<br>" +
                "With <b>artist - title (year).mp3</b> or <b>artist - title.mp3</b> as file name<br>" +
                "<br>" +
                "Use VBR option to change between constant bit rate (CBR) and variable bit rate.<br>" +
                "Large VBR files might be slow to seek in on slow devices.<br>" +
                "Use mono option to force stereo tracks to converted to mono.<br>" +
                "Set gap in seconds to add extra silence between tracks.<br>" +
                "<br>" +
                "<b>Command Line Arguments</b><br>" +
                "toMP3.jar can be run from the command line to do the encoding automatically and quit when it has finished.<br>" +
                "Use only ascii characters on Windows." +
                "<pre>" +
                "--src  [source]            source directory with audio files<br>" +
                "--dest [destination]       destination directory for target file<br>" +
                "--cover [filename]         track cover image (optional)<br>" +
                "--artist [name]            artist name<br>" +
                "--title [name]             album and title name<br>" +
                "--comment [comment tag]    comment string (optional)<br>" +
                "--year [recording year]    track year (optional, 1 - 2100)<br>" +
                "--genre [genre]            genre string (default Audiobook, optional)<br>" +
                "--bitrate [mp3 bitrate]    bitrate for target file (default 48, 24 - 320, optional)<br>" +
                "--gap [SECONDS]            insert silence between tracks (1 - 5 seconds, optional)<br>" +
                "--mono                     convert stereo to mono (optional)<br>" +
                "--vbr                      use VBR mode (optional)<br>" +
                "--auto                     start automatically and quit after successful encoding<br>" +
                "--auto2                    start automatically and quit even for error<br>" +
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
    private val bitrateLabel      = JLabel("Bitrate:")
    private val bitrateInput      = JTextField()
    private val gapLabel          = JLabel("Gap:")
    private val gapInput          = JTextField()
    private val monoLabel         = JLabel("Mono:")
    private val monoCheck         = JCheckBox()
    private val vbrLabel          = JLabel("VBR:")
    private val vbrCheck          = JCheckBox()
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
        main.add(logButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(titleLabel, x = 1, y = y, w = w, h = 4)
        main.add(titleInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(convertButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(commentLabel, x = 1, y = y, w = w, h = 4)
        main.add(commentInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(aboutButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(yearLabel, x = 1, y = y, w = w, h = 4)
        main.add(yearInput, x = w + 2, y = y, w = -22, h = 4)
        main.add(quitButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        main.add(genreLabel, x = 1, y = y, w = w, h = 4)
        main.add(genreInput, x = w + 2, y = y, w = -22, h = 4)

        y += 5
        main.add(bitrateLabel, x = 1, y = y, w = w, h = 4)
        main.add(bitrateInput, x = w + 2, y = y, w = -22, h = 4)

        y += 5
        main.add(gapLabel, x = 1, y = y, w = w, h = 4)
        main.add(gapInput, x = w + 2, y = y, w = -22, h = 4)

        y += 5
        main.add(monoLabel, x = 1, y = y, w = w, h = 4)
        main.add(monoCheck, x = w + 2, y = y, w = -22, h = 4)

        y += 5
        main.add(vbrLabel, x = 1, y = y, w = w, h = 4)
        main.add(vbrCheck, x = w + 2, y = y, w = -22, h = 4)

        pack()

        sourceInput.toolTipText   = "Select source directory with all audio files."
        destInput.toolTipText     = "Select destination directory for the result file."
        imageInput.toolTipText    = "Select cover image file (optional)."
        authorInput.toolTipText   = "Set artist/author name."
        commentInput.toolTipText  = "Set comment string (optional)."
        titleInput.toolTipText    = "Set title/artist name."
        yearInput.toolTipText     = "Set year for the audio book (optional, 1 - 2100)."
        genreInput.toolTipText    = "Set track genre (optional)."
        bitrateInput.toolTipText  = "Set bitrate for the result file (24 - 320)."
        gapInput.toolTipText      = "Insert silence between tracks (1 - 5 seconds)."
        monoCheck.toolTipText     = "Force mono if input tracks are stereo."
        vbrCheck.toolTipText      = "Use variable bit rate for the result file."

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
    fun quit() {
        prefSave()
        isVisible = false
        dispose()
        exitProcess(status = 0)
    }

    //--------------------------------------------------------------------------
    private fun stage1SetParameters(): Parameters {
        val parameters = Parameters(
            source  = sourceInput.text,
            dest    = destInput.text,
            cover   = imageInput.text,
            author  = authorInput.text,
            title   = titleInput.text,
            year    = yearInput.text,
            comment = commentInput.text,
            genre   = genreInput.text,
            bitrate = bitrateInput.text,
            gap     = gapInput.text,
            mono    = monoCheck.isSelected,
            vbr     = vbrCheck.isSelected
        )

        parameters.validate()
        return parameters
    }

    //--------------------------------------------------------------------------
    private fun stage2LoadFiles(parameters: Parameters) {
        val files = File(parameters.source).listFiles()

        if (files != null) {
            parameters.audioFiles = files.filter { file ->
                file.isFile && (file.extension.toLowerCase() == "mp3" ||
                                file.extension.toLowerCase() == "aac" ||
                                file.extension.toLowerCase() == "flac" ||
                                file.extension.toLowerCase() == "ogg" ||
                                file.extension.toLowerCase() == "wav" ||
                                file.extension.toLowerCase() == "avi" ||
                                file.extension.toLowerCase() == "mkv" ||
                                file.extension.toLowerCase() == "mp4")
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
    fun prefLoad(args: Array<String>) {
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

        try {
            val source  = args.findString("--src", "")
            val dest    = args.findString("--dest", "")
            val cover   = args.findString("--cover", "")
            val author  = args.findString("--author", "")
            val artist  = args.findString("--artist", "")
            val title   = args.findString("--title", "")
            val comment = args.findString("--comment", "")
            val year    = args.findString("--year", "")
            val genre   = args.findString("--genre", "Audiobook")
            val bitrate = args.findInt("--bitrate", 48)
            val gap     = args.findInt("--gap", 0)
            val mono    = args.find("--mono") != -1
            val vbr     = args.find("--vbr") != -1

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

            bitrateInput.text = bitrate.toString()
            gapInput.text = gap.toString()

            if (mono == true) {
                monoCheck.isSelected = true
            }

            if (vbr == true) {
                vbrCheck.isSelected = true
            }
        }
        catch (e: Exception) {
        }
    }

    //--------------------------------------------------------------------------
    private fun prefSave() {
        try {
            pref.winWidth  = size.width
            pref.winHeight = size.height
            pref.winX      = location.x
            pref.winY      = location.y
            pref.winMax    = (extendedState and Frame.MAXIMIZED_BOTH != 0)

            pref.flush()
        }
        catch (e: Exception) {
        }
    }

    //--------------------------------------------------------------------------
    fun run() {
        var parameters = Parameters()

        try {
            Swing.logMessage = ""
            parameters = stage1SetParameters()
            stage2LoadFiles(parameters)
            stage3Transcoding(parameters)

            Swing.logMessage = "transcoding finished successfully with file ${parameters.mp3.name}"

            if (auto != 0) {
                quit()
            }
            else {
                JOptionPane.showMessageDialog(this, "transcoding finished successfully with file ${parameters.mp3.name}", APP_NAME, JOptionPane.INFORMATION_MESSAGE)
            }
        }
        catch (e: Exception) {
            parameters.mp3.remove()

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
