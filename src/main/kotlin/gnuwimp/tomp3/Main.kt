/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.tomp3

import gnuwimp.swing.Swing
import java.awt.Font
import java.awt.Image
import java.awt.Toolkit
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

//------------------------------------------------------------------------------
object Main {
    val icon: Image
    private val window: MainWindow

    init {
        try {
            Swing.setup(appName = MainWindow.ABOUT_APP, aboutText = MainWindow.ABOUT_TEXT, quitLambda = { quit() })

            icon          = "gnuwimp/tomp3/tomp3.png".loadImageFromResource()
            Swing.bigFont = Font(Font.SANS_SERIF, Font.PLAIN, 24)
            Swing.defFont = Font(Font.SANS_SERIF, Font.PLAIN, 12)
            window        = MainWindow()
        }
        catch(e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(null, e, MainWindow.APP_NAME, JOptionPane.ERROR_MESSAGE)
            exitProcess(status = 1)
        }
    }

    //--------------------------------------------------------------------------
    private fun String.loadImageFromResource(): Image {
        val classLoader = Main::class.java.classLoader
        val pathShell = classLoader.getResource(this)
        return Toolkit.getDefaultToolkit().getImage(pathShell)
    }

    //--------------------------------------------------------------------------
    private fun quit() {
        window.quit()
    }

    //--------------------------------------------------------------------------
    @JvmStatic fun main(args: Array<String>) {
        try {
            SwingUtilities.invokeLater {
                window.prefLoad()
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                window.isVisible = true

                if (window.argLoad(args) == true && window.auto != 0) {
                    window.run()
                }
            }
        }
        catch(e: Exception) {
            JOptionPane.showMessageDialog(null, e, MainWindow.APP_NAME, JOptionPane.ERROR_MESSAGE)
        }
    }
}
