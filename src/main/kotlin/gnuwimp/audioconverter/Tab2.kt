/*
 * Copyright 2021 - 2025 gnuwimp@gmail.com
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
    private val sourceLabel    = JLabel("Source:")
    private val sourceInput    = JTextField()
    private val sourceButton   = JButton("Browse")
    private val destLabel      = JLabel("Destination:")
    private val destInput      = JTextField()
    private val destButton     = JButton("Browse")
    private val encoderLabel   = JLabel("Encoder:")
    private val encoderCombo   = ComboBox<String>(strings = Encoders.toNames, Encoders.DEFAULT.encoderIndex)
    private val threadsLabel   = JLabel("Threads:")
    private val threadsCombo   = ComboBox<String>(strings = Constants.TAB2_THREADS, 0)
    private val overwriteLabel = JLabel("Overwrite:")
    private val overwriteCombo = ComboBox<String>(strings = listOf("Don't overwrite existing files", "Overwrite older files", "Overwrite all"), 0)
    private val helpButton     = JButton("Help")
    private val convertButton  = JButton("Convert")
    var         auto           = Constants.Auto.NO

    //--------------------------------------------------------------------------
    init {
        val w = 16
        var y = 1

        add(sourceLabel, x = 1, y = y, w = w, h = 4)
        add(sourceInput, x = w + 2, y = y, w = -22, h = 4)
        add(sourceButton, x = -20, y = y, w = -1, h = 4)

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

        y += 5
        add(overwriteLabel, x = 1, y = y, w = w, h = 4)
        add(overwriteCombo, x = w + 2, y = y, w = 30, h = 4)

        sourceInput.toolTipText    = Constants.TAB2_STARTINPUT_TOOLTIP
        destInput.toolTipText     = Constants.TAB2_DESTINPUT_TOOLTIP
        encoderCombo.toolTipText  = Constants.ENCODERCOMBO_TOOLTIP
        threadsCombo.toolTipText  = Constants.TAB2_THREADS_TOOLTIP

        sourceButton.toolTipText   = Constants.TAB2_STARTINPUT_TOOLTIP
        destButton.toolTipText    = Constants.TAB2_DESTINPUT_TOOLTIP
        convertButton.toolTipText = Constants.CONVERTBUTTON_TOOLTIP

        //----------------------------------------------------------------------
        convertButton.addActionListener {
            run()
        }

        //----------------------------------------------------------------------
        destButton.addActionListener {
            val dialog = JFileChooser(Main.pref.getFile(destInput.text, Main.pref.tab2DestFile))

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.fontForAll        = Swing.defFont

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory == true) {
                destInput.text         = dialog.selectedFile.canonicalPath
                Main.pref.tab2DestPath = dialog.selectedFile.canonicalPath
            }
        }

        //----------------------------------------------------------------------
        helpButton.addActionListener {
            AboutHandler(appName = Constants.HELP, aboutText = Constants.TAB2_HELP_TEXT).show(parent = Main.window)
        }

        //----------------------------------------------------------------------
        sourceButton.addActionListener {
            val dialog = JFileChooser(Main.pref.getFile(sourceInput.text, Main.pref.tab2SourceFile))

            dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialog.fontForAll        = Swing.defFont

            if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && dialog.selectedFile.isDirectory == true) {
                sourceInput.text         = dialog.selectedFile.canonicalPath
                Main.pref.tab2SourcePath = dialog.selectedFile.canonicalPath
            }
        }
    }

    //--------------------------------------------------------------------------
    fun argLoad(args: Array<String>): Boolean {
        try {
            val start      = args.findString("--src", "")
            val dest       = args.findString("--dest", "")
            val encoder    = args.findInt("--encoder", Encoders.DEFAULT.encoderIndex.toLong()).toInt()
            val threads    = args.findString("--threads", "1")
            var threads2   = threads
            val overwrite  = args.findString("--overwrite", "0")
            var overwrite2 = overwrite

            if (args.find("--auto2") != -1) {
                auto = Constants.Auto.YES_QUIT_ON_ERROR
            }
            else if (args.find("--auto") != -1) {
                auto = Constants.Auto.YES_STOP_ON_ERROR
            }

            if (start != "") {
                sourceInput.text = start
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

            for ((index, choice) in Constants.TAB2_OVERWRITE.withIndex()) {
                if (overwrite == choice) {
                    overwriteCombo.selectedIndex = index
                    overwrite2 = ""
                    break
                }
            }

            if (overwrite2 != "") {
                throw Exception("error: invalid value for --overwrite ($overwrite)")
            }

            return true
        }
        catch (e: Exception) {
            if (auto == Constants.Auto.YES_QUIT_ON_ERROR) {
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
            source    = sourceInput.text,
            dest      = destInput.text,
            encoder   = Encoders.toEncoder(encoderCombo.selectedIndex),
            threads   = threadsCombo.text.toInt(),
            overwrite = if (overwriteCombo.selectedIndex == 1) Constants.Overwrite.OLDER else if (overwriteCombo.selectedIndex == 2) Constants.Overwrite.ALL else Constants.Overwrite.NO
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
            val infile  = parameters.inputFiles[index]
            val outfile = parameters.outputFiles[index]

            if (outfile.isMissing == true && outfiles[outfile.filename] == null) {
                outfiles[outfile.filename] = true
                tasks.add(Tab2Task(parameters.inputFiles[index], outfile, parameters))
            }
            else if (parameters.overwrite == Constants.Overwrite.OLDER && outfile.mod < infile.mod) {
                outfiles[outfile.filename] = true
                tasks.add(Tab2Task(parameters.inputFiles[index], outfile, parameters))
            }
            else if (parameters.overwrite == Constants.Overwrite.ALL) {
                outfiles[outfile.filename] = true
                tasks.add(Tab2Task(parameters.inputFiles[index], outfile, parameters))
            }
        }

        if (tasks.isEmpty() == true) {
            throw FileExistException("error: no files to convert\nall files already converted")
        }

        return tasks
    }

    //--------------------------------------------------------------------------
    private fun stage5Transcoding(parameters: Tab2Parameters, tasks: List<Task>) {
        val progress = ConvertManager(tasks = tasks, maxThreads = parameters.threads, onError = TaskManager.Execution.STOP_JOIN, onCancel = TaskManager.Execution.STOP_JOIN)
        val dialog   = TaskDialog(taskManager = progress, title = "Converting Files", type = TaskDialog.Type.PERCENT, parent = Main.window, height = Swing.defFont.size * 26)

        dialog.enableCancel = true
        ConvertManager.clear()
        dialog.start(updateTime = 200L, messages = parameters.threads + 1)
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

            if (auto != Constants.Auto.NO) {
                Main.window.quit()
            }
            else {
                JOptionPane.showMessageDialog(this, message, Constants.APP_NAME, JOptionPane.INFORMATION_MESSAGE)
            }
        }
        catch (e: FileExistException) {
            if (auto != Constants.Auto.NO) {
                println("${e.message}")
                Main.window.quit()
            }
            else {
                Swing.errorMessage = e.message ?: "!"
                JOptionPane.showMessageDialog(this, e.message, Constants.APP_NAME, JOptionPane.ERROR_MESSAGE)
            }
        }
        catch (e: Exception) {
            if (auto == Constants.Auto.YES_QUIT_ON_ERROR) {
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
