/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Dimension
import javax.swing.*

/**
 * A dialog for displaying some html text.
 */
class HTMLDialog(html: String, parent: JFrame? = null, title: String = "", modal: Boolean = true, width: Int = Swing.defFont.size * 60, height: Int = Swing.defFont.size * 50) : BaseDialog(parent, title, modal) {
    init {
        val panel       = LayoutPanel(size = Swing.defFont.size / 2)
        val label       = JLabel(html)
        val scroll      = JScrollPane(label)
        val closeButton = JButton("Close")

        add(panel)
        panel.add(scroll,      x =   1,  y =  1,  w = -1,  h = -6)
        panel.add(closeButton, x = -21,  y = -5,  w = 20,  h =  4)
        pack()

        label.lineBorder        = true
        label.verticalAlignment = SwingConstants.TOP
        size                    = Dimension(width, height)
        fontForAll              = Swing.defFont

        centerWindow()

        //----------------------------------------------------------------------
        closeButton.addActionListener {
            hideAndDispose()
        }
    }
}
