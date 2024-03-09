/*
 * Copyright 2016 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea

/**
 * A dialog for displaying text
 */
class TextDialog(text: String, showLastLine: Boolean = false, parent: JFrame? = null, title: String = "", modal: Boolean = true, width: Int = Swing.defFont.size * 60, height: Int = Swing.defFont.size * 50) : BaseDialog(parent, title, modal) {
    init {
        val panel       = LayoutPanel(size = Swing.defFont.size / 2)
        val textView    = JTextArea(text)
        val closeButton = JButton("Close")
        val scroll      = JScrollPane(textView)

        add(panel)
        panel.add(scroll,       x =   1,  y =  1,  w = -1,  h = -6)
        panel.add(closeButton,  x = -21,  y = -5,  w = 20,  h =  4)
        pack()

        fontForAll          = Swing.defFont
        textView.isEditable = false
        size                = Dimension(width, height)
        centerWindow()

        if (showLastLine && textView.document.length > 0)
            textView.caretPosition = textView.document.length - 1

        closeButton.addActionListener {
            hideAndDispose()
        }
    }
}
