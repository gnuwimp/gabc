/*
 * Copyright 2016 - 2019 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.gabc

import gnuwimp.core.extension.isImage
import gnuwimp.core.extension.tryDelete
import gnuwimp.core.swing.AboutHandler
import gnuwimp.core.swing.ComboBoxCallback
import gnuwimp.core.swing.LayoutPanel
import gnuwimp.core.swing.Platform
import gnuwimp.core.swing.dialog.ImageFileDialog
import gnuwimp.core.swing.dialog.ProgressDialog
import gnuwimp.core.swing.dialog.TextDialog
import gnuwimp.core.swing.extension.setFontForAll
import gnuwimp.core.util.Progress
import gnuwimp.core.util.ProgressAction
import gnuwimp.core.util.Task
import gnuwimp.core.util.throwFirstError
import java.awt.Frame
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.util.prefs.Preferences
import javax.swing.*
import kotlin.system.exitProcess

/**
 * Create and show main window for gABC
 */
class MainWindow : JFrame(appName) {
    companion object {
        const val debug = false
        const val appName = "gABC"
        const val aboutApp = "About gABC"
        const val about: String = "<html>" +
                "<h2>gABC 2.0</h2>" +
                "<font color=\"blue\">" +
                "Copyright 2016 - 2019 gnuwimp@gmail.com.<br>" +
                "Released under the GNU General Public License v3.0.<br>" +
                "See https://github.com/gnuwimp/gabc.<br>" +
                "</font>" +
                "<br>" +
                "<b>gABC</b> is an audio book converter written in Kotlin.<br>" +
                "Use gABC with caution and at your own risk.<br>" +
                "<br>" +
                "gABC transcodes one or more mp3 files into one mp3 file.<br>" +
                "It uses lame for encoding/decoding which must be in the search path.<br>" +
                "Download lame from http://lame.sourceforge.net<br>" +
                "<br>" +
                "Select a source directory with mp3 files or an CD.<br>" +
                "All mp3 files in that directory will be converted to one mp3 file.<br>" +
                "They must have names that make them sorted in playing order.<br>" +
                "And all mp3 files must have same audio properties (mono/stereo/samplerate).<br>" +
                "The finished audio file will end up in the destination directory.<br>" +
                "With 'author - title (year).mp3' as file name<br>" +
                "Genre will be set to Audiobook.<br>" +
                "The result file will always be in mono and CBR encoded.<br>" +
                "</html>"
    }

    private val pref: Preferences = Preferences.userNodeForPackage(Main.javaClass)
    private var sourceDir         = File(System.getProperty("user.home"))
    private var destDir           = File(System.getProperty("user.home"))
    private var imageDir          = File(System.getProperty("user.home"))
    private val main              = LayoutPanel(size = Platform.defFont.size / 2)
    private val sourceLabel       = JLabel("Source:")
    private val sourceInput       = JTextField()
    private val sourceButton      = JButton("Browse")
    private val destLabel         = JLabel("Destination:")
    private val destInput         = JTextField()
    private val destButton        = JButton("Browse")
    private val imageLabel        = JLabel("Cover Image:")
    private val imageInput        = JTextField()
    private val imageButton       = JButton("Browse")
    private val authorLabel       = JLabel("Author")
    private val authorInput       = JTextField()
    private val titleLabel        = JLabel("Title:")
    private val titleInput        = JTextField()
    private val commentLabel      = JLabel("Comment:")
    private val commentInput      = JTextField()
    private val yearLabel         = JLabel("Year:")
    private val yearInput         = JTextField()
    private val bitrateLabel      = JLabel("Bitrate:")
    private val bitrateCombo      = ComboBoxCallback<String>(strings = listOf("24", "32", "40", "48", "56", "64", "80", "96", "112", "128", "192", "256", "320"), index = 1)
    private val aboutButton       = JButton("About")
    private val logButton         = JButton("Show Log")
    private val convertButton     = JButton("Convert")
    private val quitButton        = JButton("Quit")

    /*
     * Create window and event listeners
     */
    init {
        iconImage                  = Main.icon
        contentPane                = main
        bitrateCombo.selectedIndex = 2
        sourceInput.isEditable     = false
        destInput.isEditable       = false
        imageInput.isEditable      = false

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
        main.add(bitrateLabel, x = 1, y = y, w = w, h = 4)
        main.add(bitrateCombo, x = w + 2, y = y, w = -22, h = 4)

        authorInput.toolTipText   = "Set author for the audio book."
        bitrateCombo.toolTipText  = "Set bitrate for the MP3 file."
        commentInput.toolTipText  = "Set comment for the audio book (optional)."
        convertButton.toolTipText = "Start transcoding."
        destButton.toolTipText    = "Select destination directory for the result mp3 file."
        destInput.toolTipText     = "Select destination directory for the result mp3 file."
        imageButton.toolTipText   = "Select cover image (optional)."
        imageInput.toolTipText    = "Select cover image (optional)."
        logButton.toolTipText     = "Show log window with all tasks that have been executed."
        sourceButton.toolTipText  = "Select source directory with all source mp3 files."
        sourceInput.toolTipText   = "Select source directory with all source mp3 files."
        titleInput.toolTipText    = "Set title for the audio book."
        yearInput.toolTipText     = "Set year for the audio book."

        if (debug) {
            sourceInput.text = "D:\\Tmp"
            destInput.text = "D:\\Tmp"
            imageInput.text = "D:\\Tmp\\cover.jpg"
            authorInput.text = "J.K. Rowling"
            titleInput.text = "Best of"
            commentInput.text = "Read by Gnu"
            yearInput.text = "1666"
            bitrateCombo.selectedIndex = 3
        }

        // Quit application
        addWindowListener( object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                quit()
            }
        })

        // Show about dialog box
        aboutButton.addActionListener {
            AboutHandler(appName = aboutApp, aboutText = about).show(parent = this)
        }

        // Start conversion of mp3 files
        convertButton.addActionListener {
            var parameters = Parameters()

            try {
                Platform.logMessage = ""
                parameters = stage1SetParameters()
                stage2LoadFiles(parameters)
                stage3Transcoding(parameters)

                Platform.logMessage = "transcoding finished successfully with file ${parameters.mp3.name}"
                JOptionPane.showMessageDialog(this, "transcoding finished successfully with file ${parameters.mp3.name}", appName, JOptionPane.INFORMATION_MESSAGE)
            }
            catch (e: Exception) {
                Platform.logMessage = e.message ?: "!"
                JOptionPane.showMessageDialog(this, e.message, appName, JOptionPane.ERROR_MESSAGE)
                parameters.mp3.tryDelete(maxTries = 100)
            }
            finally {
                System.gc()
            }
        }

        // Select destination directory and save selected directory to preference
        destButton.addActionListener {
            val dialog = JFileChooser(destDir)

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.setFontForAll(this.font)

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory) {
                destDir = dialog.selectedFile
                destInput.text = dialog.selectedFile.canonicalPath
                pref.destPath = destDir.canonicalPath
            }
        }

        // Select cover image and save image directory to preference
        imageButton.addActionListener {
            val dialog = ImageFileDialog(imageDir.canonicalPath, this)

            dialog.setFontForAll(this.font)
            val file = dialog.file

            if (file != null && file.isImage) {
                imageDir = file.parentFile
                imageInput.text = file.canonicalPath
                pref.imagePath = file.parentFile.canonicalPath
            }
            else
                imageInput.text = ""
        }

        // Show log dialog
        logButton.addActionListener {
            val dialog = TextDialog(text = Platform.logMessage, showLastLine = true, title = "Log", parent = this)

            dialog.isVisible = true
        }

        // Select source directory and save selected directory to preference
        sourceButton.addActionListener {
            val dialog = JFileChooser(sourceDir)

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.setFontForAll(this.font)

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory) {
                sourceDir = dialog.selectedFile
                sourceInput.text = dialog.selectedFile.canonicalPath
                pref.sourcePath = sourceDir.canonicalPath
            }
        }

        // Quit gABC
        quitButton.addActionListener {
            quit()
        }
    }

    /**
     * Quit gABC and save also all preferences
     */
    fun quit() {
        windowSave()
        isVisible = false
        dispose()
        exitProcess(status = 0)
    }

    /**
     * Check input parameters
     */
    private fun stage1SetParameters(): Parameters {
        val parameters = Parameters(
                source  = sourceInput.text,
                dest    = destInput.text,
                cover   = imageInput.text,
                author  = authorInput.text,
                title   = titleInput.text,
                year    = yearInput.text,
                comment = commentInput.text,
                bitrate = bitrateCombo.text
        )

        parameters.check()
        return parameters
    }

    /**
     * Load files from disk
     */
    private fun stage2LoadFiles(parameters: Parameters) {
        val files = File(parameters.source).listFiles()

        if (files != null) {
            parameters.mp3Files = files.filter { file ->
                file.isFile && file.extension.toLowerCase() == "mp3"
            }
        }

        if (parameters.mp3Files.isEmpty())
            throw Exception("error: no mp3 files in source directory")
    }

    /**
     * Transcode mp3 files to one file
     */
    private fun stage3Transcoding(parameters: Parameters) {
        val tasks = mutableListOf<Task>()
        tasks.add(TranscoderTask(parameters = parameters))
        val progress = Progress(tasks = tasks, threadCount = 1)
        val dialog = ProgressDialog(progress = progress, title = "Transcoding Files", parent = this)

        dialog.enableCancel = true
        dialog.start(actionOnError = ProgressAction.JOIN, actionOnCancel = ProgressAction.JOIN, updateTime = 50L, setTitlePercent = true)
        tasks.throwFirstError()
    }

    /**
     * Restore some application data when starting program
     */
    fun windowRestore() {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        pack()
        setFontForAll(Platform.defFont)

        val w  = pref.winWidth
        val h  = pref.winHeight
        var x  = pref.winX
        var y  = pref.winY
        val sc = Toolkit.getDefaultToolkit().screenSize

        if (x > sc.getWidth() || x < -50)
            x = 0

        if (y > sc.getHeight() || y < -50)
            y = 0

        setLocation(x, y)
        setSize(w, h)

        if (pref.winMax)
            extendedState = Frame.MAXIMIZED_BOTH

        destDir = File(pref.destPath)
        sourceDir = File(pref.sourcePath)
        imageDir = File(pref.imagePath)
    }

    /**
     * Save windows size and last used directories
     */
    private fun windowSave() {
        try {
            val size = size
            val pos  = location

            pref.winWidth = size.width
            pref.winHeight = size.height
            pref.winX = pos.x
            pref.winY = pos.y
            pref.winMax = (extendedState and Frame.MAXIMIZED_BOTH != 0)
            pref.flush()
        }
        catch (e: Exception) {
        }
    }
}
