/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import gnuwimp.util.TaskManager
import gnuwimp.util.TimeFormat
import gnuwimp.util.format
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JProgressBar
import kotlin.concurrent.timer

/**
 * A dialog window that runs a TaskManager object.
 * Cancel button is disabled by default.
 */
class TaskDialog(val taskManager: TaskManager, val type: Type, parent: JFrame? = null, title: String = "Working...", modal: Boolean = true, width: Int = Swing.defFont.size * 60, height: Int = Swing.defFont.size * 16) : BaseDialog(parent, title, modal) {
    enum class Type {
        MESSAGE_ONLY,  /** Show only messages */
        PERCENT,       /** Show messages and a prercent progress bar */
        PERCENT_ONLY,  /** Show only progress bar */
        PROGRESS,      /** Show messages and the progress counter label */
        PROGRESS_ONLY, /** Show messages and the progress counter label */
        TIME,          /** Show messages and running time */
        TIME_ONLY,     /** Show only running time */
    }

    private var cancel        = false
    private var cancelIsUsed  = false
    private var percent       = 0
    private var message       = ""
    private val cancelButton  = JButton("Cancel")
    private val progressBar   = JProgressBar(0, 100)
    private val progressLabel = JLabel("")
    private val messageLabel  = JLabel("")

    /**
     * Enable or disable cancel button
     */
    var enableCancel: Boolean
        get() = cancelButton.isEnabled

        set(value) {
            cancelButton.isEnabled = value
        }

    init {
        val panel   = LayoutPanel(size = Swing.defFont.size / 2)
        var height2 = height
        var width2  = width

        progressLabel.horizontalAlignment = JLabel.CENTER
        progressLabel.verticalAlignment   = JLabel.TOP
        progressLabel.lineGrayBorder      = true
        progressBar.isStringPainted       = true
        messageLabel.lineGrayBorder       = true
        messageLabel.verticalAlignment    = JLabel.TOP
        cancelButton.isEnabled            = false
        cancelButton.grabFocus()

        add(panel)

        when (type) {
            Type.MESSAGE_ONLY -> {
                progressBar.isVisible = false
                progressLabel.isVisible = false
                panel.add(messageLabel,  x =   1,  y =  1,  w = -1,  h = -6)
                panel.add(cancelButton,  x = -21,  y = -5,  w = 20,  h =  4)
            }
            Type.PERCENT -> {
                progressLabel.isVisible = false
                panel.add(progressBar,   x =   1,  y =  1,  w = -1,  h =  4)
                panel.add(messageLabel,  x =   1,  y =  6,  w = -1,  h = -6)
                panel.add(cancelButton,  x = -21,  y = -5,  w = 20,  h =  4)
            }
            Type.PROGRESS, Type.TIME -> {
                progressBar.isVisible = false
                panel.add(progressLabel, x =   1,  y =  1,  w = -1,  h =  6)
                panel.add(messageLabel,  x =   1,  y =  8,  w = -1,  h = -6)
                panel.add(cancelButton,  x = -21,  y = -5,  w = 20,  h =  4)
            }
            Type.PERCENT_ONLY -> {
                height2 = Swing.defFont.size * 9
                width2 = Swing.defFont.size * 40
                progressLabel.isVisible = false
                messageLabel.isVisible = false
                panel.add(progressBar,   x =   1,  y =  1,  w = -1,  h =  5)
                panel.add(cancelButton,  x = -21,  y = -5,  w = 20,  h =  4)
            }
            Type.TIME_ONLY -> {
                height2 = Swing.defFont.size * 9
                width2 = Swing.defFont.size * 40
                progressBar.isVisible = false
                messageLabel.isVisible = false
                panel.add(progressLabel, x =   1,  y =  1,  w = -1,  h = 5)
                panel.add(cancelButton,  x = -21,  y = -5,  w = 20,  h =  4)
            }
            Type.PROGRESS_ONLY -> {
                height2 = Swing.defFont.size * 9
                width2 = Swing.defFont.size * 40
                progressBar.isVisible = false
                messageLabel.isVisible = false
                panel.add(progressLabel, x =   1,  y =  1,  w = -1,  h = 5)
                panel.add(cancelButton,  x = -21,  y = -5,  w = 20,  h =  4)
            }
        }
        pack()

        fontForAll         = Swing.defFont
        progressLabel.font = Swing.bigFont
        size               = Dimension(width2, height2)

        centerWindow()

        //----------------------------------------------------------------------
        cancelButton.addActionListener {
            cancel = true
        }
    }

    /**
     * Start timer that executes all task(s) and thread(s)
     * Timer will also update dialog box info
     */
    fun start(updateTime: Long = 100, messages: Int = 4) {
        val start = System.currentTimeMillis()

        timer(period = updateTime, action = {
            if (taskManager.run(cancel) == true) {
                if (type != Type.TIME_ONLY && type != Type.PERCENT_ONLY) {
                    val buffer ="<html><pre>" + taskManager.message(messages) + "</pre><html/>"

                    if (buffer != message) {
                        message           = buffer
                        messageLabel.text = message
                    }
                }

                if ((type == Type.PERCENT || type == Type.PERCENT_ONLY) && taskManager.percent != percent) {
                    percent           = taskManager.percent
                    progressBar.value = percent
                }
                else if (type == Type.PROGRESS || type == Type.PROGRESS_ONLY) {
                    progressLabel.text = taskManager.progress.format(" ")
                }
                else if (type == Type.TIME || type == Type.TIME_ONLY) {
                    progressLabel.text = TimeFormat.LONG_TIME.format((System.currentTimeMillis() - start), "UTC")
                }
            }
            else {
                cancel()
                Thread.sleep(200)
                hideAndDispose()
            }
        })

        isVisible = true
    }
}
