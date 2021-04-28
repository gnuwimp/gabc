/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Point
import java.awt.Toolkit
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * Base dialog window.
 * Dialog window cant be closed with window button.
 */
abstract class BaseDialog(val parent: JFrame?, title: String, modal: Boolean) : JDialog(parent, title, modal) {
    init {
         defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }

    /**
     * Center dialog window.
     * If it has no parent then it will be centered on screen.
     * Otherwise it will be centered on parent window.
     */
    fun centerWindow() {
        val sdim = Toolkit.getDefaultToolkit().screenSize
        val dim  = size
        var xpos = sdim.width / 2
        var ypos = sdim.height / 2

        if (parent != null) {
            val pdim = parent.size

            if (pdim.height > 100 && pdim.width > 100) {
                xpos = parent.location.x + pdim.width / 2
                ypos = parent.location.y + pdim.height / 2
            }
        }

        location = Point(xpos - (dim.width / 2), ypos - (dim.height / 2))
    }

    /**
     * Center dialog window on top of parent window.
     * Works only if dialog has an parent.
     */
    fun centerWindowOnTop() {
        require(parent != null)

        val dim  = parent.size
        val xpos = parent.location.x + dim.width / 2
        val ypos = parent.location.y

        location = Point(xpos - (width / 2), ypos)
    }

    /**
     * Hide dialog and delete resources.
     */
    fun hideAndDispose() {
        isVisible = false
        dispose()
    }
}
