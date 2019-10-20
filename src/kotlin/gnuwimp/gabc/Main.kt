/*
 * Copyright 2016 - 2019 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.gabc

import gnuwimp.core.swing.Platform
import java.awt.Image
import java.awt.Toolkit
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

/**
 * Start gABC here
 */
object Main {
    val icon: Image
    private val window: MainWindow

    init {
        try {
            // Platform.defFont = Font(Font.MONOSPACED, Font.PLAIN, 18)
            Platform.setup(appName = MainWindow.aboutApp, aboutText = MainWindow.about, quitHandler = { quit() })

            icon = "gnuwimp/gabc/resource/icon.png".loadImageFromResource()
            window = MainWindow()
        }
        catch(e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(null, e, MainWindow.appName, JOptionPane.ERROR_MESSAGE)
            exitProcess(status = 1)
        }
    }

    /**
     * Load icons from jar file
     */
    private fun String.loadImageFromResource(): Image {
        val classLoader = Main::class.java.classLoader
        val pathShell = classLoader.getResource(this)
        return Toolkit.getDefaultToolkit().getImage(pathShell)
    }

    /**
     * Quit for macOS
     */
    private fun quit() {
        window.quit()
    }

    /**
     * Main
     */
    @JvmStatic fun main(args: Array<String>) {
        try {
            SwingUtilities.invokeLater {
                window.isVisible = true
                window.windowRestore()
            }
        }
        catch(e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(null, e, MainWindow.appName, JOptionPane.ERROR_MESSAGE)
        }
    }
}
