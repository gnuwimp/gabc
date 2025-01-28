/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.swing.*
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JTabbedPane
import kotlin.system.exitProcess

//------------------------------------------------------------------------------
class MainWindow : JFrame(Constants.APP_NAME) {
    private val main        = LayoutPanel(size = Swing.defFont.size / 2)
    val         tabs        = JTabbedPane()
    val         tab1        = Tab1()
    val         tab2        = Tab2()
    private val quitButton  = JButton("Quit")
    private val aboutButton = JButton("About")
    private val logButton   = JButton("Show Log")

    //--------------------------------------------------------------------------
    init {
        iconImage   = Main.icon
        contentPane = main

        tabs.border = BorderFactory.createEmptyBorder(4, 4, 0, 4)
        tab1.border = BorderFactory.createEtchedBorder()
        tab2.border = BorderFactory.createEtchedBorder()

        tabs.addTab(Constants.TAB1_LABEL, null, tab1, Constants.TAB1_TOOLTIP)
        tabs.addTab(Constants.TAB2_LABEL, null, tab2, Constants.TAB2_TOOLTIP)
        main.add(tabs, 0, 0, 0, -6)
        main.add(quitButton, x = -21, y = -5, w = 20, h = 4)
        main.add(aboutButton, x = -42, y = -5, w = 20, h = 4)
        main.add(logButton, x = -63, y = -5, w = 20, h = 4)

        logButton.toolTipText = Constants.LOGBUTTON_TOOLTIP

        pack()

        //----------------------------------------------------------------------
        addWindowListener( object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                quit()
            }
        })

        //----------------------------------------------------------------------
        aboutButton.addActionListener {
            AboutHandler(appName = Constants.ABOUT_APP, aboutText = Constants.aboutApp()).show(parent = Main.window, height = Swing.defFont.size * 55)
        }

        //----------------------------------------------------------------------
        logButton.addActionListener {
            val dialog = TextDialog(text = Swing.logMessage + "\n\n" + Swing.errorMessage, showLastLine = true, title = "Log", parent = Main.window)

            dialog.isVisible = true
        }

        //----------------------------------------------------------------------
        quitButton.addActionListener {
            Main.window.quit()
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
    fun prefLoad() {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        fontForAll            = Swing.defFont

        val w  = Main.pref.winWidth
        val h  = Main.pref.winHeight
        var x  = Main.pref.winX
        var y  = Main.pref.winY
        val sc = Toolkit.getDefaultToolkit().screenSize

        if (x > sc.getWidth() || x < -50) {
            x = 0
        }

        if (y > sc.getHeight() || y < -50) {
            y = 0
        }

        setLocation(x, y)
        setSize(w, h)

        if (Main.pref.winMax == true) {
            extendedState = MAXIMIZED_BOTH
        }
    }

    //--------------------------------------------------------------------------
    private fun prefSave() {
        try {
            Main.pref.winWidth  = size.width
            Main.pref.winHeight = size.height
            Main.pref.winX      = location.x
            Main.pref.winY      = location.y
            Main.pref.winMax    = (extendedState and MAXIMIZED_BOTH != 0)

            Main.pref.flush()
        }
        catch (e: Exception) {
        }
    }
}
