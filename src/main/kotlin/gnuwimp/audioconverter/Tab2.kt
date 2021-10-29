/*
 * Copyright © 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.swing.*
import gnuwimp.util.*
import java.io.File
import javax.swing.*

//------------------------------------------------------------------------------
@Suppress("UNUSED_VALUE")
class Tab2 : LayoutPanel(size = Swing.defFont.size / 2 + 1) {
    private val startLabel    = JLabel("Source:")
    private val startInput    = JTextField()
    private val startButton   = JButton("Browse")
    private val destLabel     = JLabel("Destination:")
    private val destInput     = JTextField()
    private val destButton    = JButton("Browse")
    private val encoderLabel  = JLabel("Encoder:")
    private val encoderCombo  = ComboBox<String>(strings = Encoders.toNames, Encoders.DEFAULT.encoderIndex)
    private val threadsLabel  = JLabel("Threads:")
    private val threadsCombo  = ComboBox<String>(strings = Constants.TAB2_THREADS, 0)
    private val helpButton    = JButton("Help")
    private val convertButton = JButton("Convert")
    var         auto          = 0

    //--------------------------------------------------------------------------
    init {
        val w = 16
        var y = 1

        add(startLabel, x = 1, y = y, w = w, h = 4)
        add(startInput, x = w + 2, y = y, w = -22, h = 4)
        add(startButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        add(destLabel, x = 1, y = y, w = w, h = 4)
        add(destInput, x = w + 2, y = y, w = -22, h = 4)
        add(destButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        add(encoderLabel, x = 1, y = y, w = w, h = 4)
        add(encoderCombo, x = w + 2, y = y, w = 30, h = 4)
        add(helpButton, x = -20, y = y, w = -1, h = 4)

        y += 5
        add(threadsLabel, x = 1, y = y, w = w, h = 4)
        add(threadsCombo, x = w + 2, y = y, w = 30, h = 4)
        add(convertButton, x = -20, y = y, w = -1, h = 4)

        startInput.toolTipText    = Constants.TAB2_STARTINPUT_TOOLTIP
        destInput.toolTipText     = Constants.TAB2_DESTINPUT_TOOLTIP
        encoderCombo.toolTipText  = Constants.ENCODERCOMBO_TOOLTIP
        threadsCombo.toolTipText  = Constants.TAB2_THREADS_TOOLTIP

        startButton.toolTipText   = Constants.TAB2_STARTINPUT_TOOLTIP
        destButton.toolTipText    = Constants.TAB2_DESTINPUT_TOOLTIP
        convertButton.toolTipText = Constants.CONVERTBUTTON_TOOLTIP

        //----------------------------------------------------------------------
        convertButton.addActionListener {
            run()
        }

        //----------------------------------------------------------------------
        destButton.addActionListener {
            val dialog = JFileChooser(Main.pref.destPath2File)

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.fontForAll        = Swing.defFont

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory) {
                destInput.text      = dialog.selectedFile.canonicalPath
                Main.pref.destPath2 = dialog.selectedFile.canonicalPath
            }
        }

        //----------------------------------------------------------------------
        helpButton.addActionListener {
            AboutHandler(appName = Constants.HELP, aboutText = Constants.TAB2_HELP_TEXT).show(parent = Main.window)
        }

        //----------------------------------------------------------------------
        startButton.addActionListener {
            val dialog = JFileChooser(Main.pref.startPathFile)

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.fontForAll        = Swing.defFont

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory) {
                startInput.text     = dialog.selectedFile.canonicalPath
                Main.pref.startPath = dialog.selectedFile.canonicalPath
            }
        }
    }

    //--------------------------------------------------------------------------
    fun argLoad(args: Array<String>): Boolean {
        try {
            val start    = args.findString("--src", "")
            val dest     = args.findString("--dest", "")
            val encoder  = args.findInt("--encoder", Encoders.DEFAULT.encoderIndex.toLong()).toInt()
            val threads  = args.findString("--threads", "1")
            var threads2 = threads

            if (args.find("--auto2") != -1) {
                auto = 2
            }
            else if (args.find("--auto") != -1) {
                auto = 1
            }

            if (start != "") {
                startInput.text = start
            }

            if (dest != "") {
                destInput.text = dest
            }

            encoderCombo.selectedIndex = encoder

            for ((index, choice) in Constants.TAB2_THREADS.withIndex()) {
                if (threads == choice) {
                    threadsCombo.selectedIndex = index
                    threads2 = ""
                    break
                }
            }

            if (threads2 != "") {
                throw Exception("error: invalid value for --threads ($threads)")
            }

            return true
        }
        catch (e: Exception) {
            if (auto == 2) {
                println(e.message)
                Main.window.quit()
            }
            else {
                JOptionPane.showMessageDialog(null, e.message, Constants.APP_NAME, JOptionPane.ERROR_MESSAGE)
            }

            return false
        }
    }

    //--------------------------------------------------------------------------
    private fun stage1SetParameters() : Tab2Parameters {
        val parameters = Tab2Parameters(
            source   = startInput.text,
            dest    = destInput.text,
            encoder = Encoders.toEncoder(encoderCombo.selectedIndex),
            threads = threadsCombo.text.toInt(),
        )

        parameters.validate()
        return parameters
    }

    //--------------------------------------------------------------------------
    private fun stage2LoadFiles(parameters: Tab2Parameters) {
        val files = FileInfo(parameters.source).readDir(FileInfo.ReadDirOption.ALL_RECURSIVE)

        parameters.inputFiles = files.filter {
            it.isAudioFile
        }

        if (parameters.inputFiles.isEmpty() == true) {
            throw Exception("error: no audio/video files found in ${parameters.source}")
        }

        parameters.inputFiles = parameters.inputFiles.sortedBy {
            it.filename
        }

        val start = FileInfo(parameters.source).canonicalPath
        val dest  = FileInfo(parameters.dest).canonicalPath

        parameters.inputFiles.forEach {
            val sourceDir = FileInfo(it.path).canonicalPath
            val destDir   = FileInfo(dest + sourceDir.replace(start, "")).canonicalPath
            val destFile  = FileInfo(destDir + File.separator + it.name.replaceAfterLast(".", parameters.encoder.fileExt))

            parameters.outputFiles.add(destFile)
        }
    }

    //--------------------------------------------------------------------------
    private fun stage3CreateDirectories(parameters: Tab2Parameters) {
        parameters.outputFiles.forEach {
            val parent = FileInfo(it.path)

            if (parent.isDir == false && parent.file.mkdir() == false) {
                throw Exception("error: can't create directory '${parent.filename}'")
            }
        }
    }

    //--------------------------------------------------------------------------
    private fun stage4CreateTasks(parameters: Tab2Parameters): List<Task> {
        val tasks    = mutableListOf<Task>()
        val outfiles = mutableMapOf<String, Boolean>()

        for (index in parameters.inputFiles.indices) {
            val outfile = parameters.outputFiles[index]

            if (outfile.isMissing == true && outfiles[outfile.filename] == null) {
                outfiles[outfile.filename] = true
                tasks.add(Tab2Task(parameters.inputFiles[index], outfile, parameters))
            }
        }

        if (tasks.isEmpty() == true) {
            throw Exception("error: no files to convert\nall files already converted")
        }

        return tasks
    }

    //--------------------------------------------------------------------------
    private fun stage5Transcoding(parameters: Tab2Parameters, tasks: List<Task>) {
        val progress = ConvertManager(tasks = tasks, threadCount = parameters.threads, onError = TaskManager.Execution.STOP_JOIN, onCancel = TaskManager.Execution.STOP_JOIN)
        val dialog   = TaskDialog(taskManager = progress, title = "Converting Files", type = TaskDialog.Type.PERCENT, parent = Main.window)

        dialog.enableCancel = true
        ConvertManager.clear()
        dialog.start(updateTime = 200L)
        tasks.throwFirstError()
    }

    //--------------------------------------------------------------------------
    fun run() {
        Swing.logMessage = ""
        Swing.errorMessage = ""

        try {
            val parameters = stage1SetParameters()
            stage2LoadFiles(parameters)
            stage3CreateDirectories(parameters)
            val tasks = stage4CreateTasks(parameters)
            stage5Transcoding(parameters, tasks)

            val message = if (Swing.hasError == true) {
                "all (${tasks.size}) files encoded successfully but there are some errors - check the log"
            }
            else {
                "all (${tasks.size}) files encoded successfully"
            }

            Swing.logMessage = message

            if (auto != 0) {
                Main.window.quit()
            }
            else {
                JOptionPane.showMessageDialog(this, message, Constants.APP_NAME, JOptionPane.INFORMATION_MESSAGE)
            }
        }
        catch (e: Exception) {
            if (auto == 2) {
                println("${e.message}")
                Main.window.quit()
            }
            else {
                Swing.errorMessage = e.message ?: "!"
                JOptionPane.showMessageDialog(this, e.message, Constants.APP_NAME, JOptionPane.ERROR_MESSAGE)
            }
        }
        finally {
            System.gc()
        }
    }
}
