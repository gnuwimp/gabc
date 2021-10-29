/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import gnuwimp.util.TimeFormat
import gnuwimp.util.format
import java.awt.Font
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import javax.swing.JFrame
import javax.swing.UIManager

/**
 * Quit handler, mostly for macOS
 */
class QuitHandler(val quit: () -> Unit) : InvocationHandler {
    /**
     * Call quit, this is called when cmd+q is pressed in macOS
     */
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        quit()
        return null
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * About handler, mostly for macOS.
 */
class AboutHandler(val appName: String, val aboutText: String) : InvocationHandler {
    /**
     * Show dialog, this is called from the mac menu
     */
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        show()
        return null
    }

    /**
     * Show about dialog.
     */
    fun show(width: Int = Swing.defFont.size * 60, height: Int = Swing.defFont.size * 50) {
        val dialog = HTMLDialog(html = aboutText, title = appName, width = width, height = height)
        dialog.isVisible = true
    }

    /**
     * Show about dialog.
     */
    fun show(parent: JFrame, width: Int = Swing.defFont.size * 60, height: Int = Swing.defFont.size * 50) {
        val dialog = HTMLDialog(html = aboutText, title = appName, parent = parent, width = width, height = height)
        dialog.isVisible = true
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * Initiate GUI stuff.
 */
object Swing {
    private var messages: MutableList<String> = mutableListOf()
    private var errors: MutableList<String>   = mutableListOf()
    private var startTime: Long               = System.currentTimeMillis()
    var         defFont: Font                 = Font(Font.SANS_SERIF, Font.PLAIN, 14)
    var         bigFont: Font                 = Font(Font.SANS_SERIF, Font.PLAIN, 28)

    /**
     * Add or get error message.
     * Use empty string to clear log.
     */
    var errorMessage: String
        get() {
            synchronized(errors) {
                return errors.joinToString("\n")
            }
        }

        set(value) {
            synchronized(errors) {
                if (value != "") {
                    errors.add(TimeFormat.LONG_TIME.format(milliSeconds = System.currentTimeMillis() - startTime, timeZone = "UTC") + "| $value")
                }
                else {
                    errors.clear()
                }
            }
        }

    /**
     * Check if error log has any messages
     */
    val hasError: Boolean
        get() {
            synchronized(errors) {
                return errors.size > 0
            }
        }

    /**
     * Return true if java is running on macOS.
     */
    val isMac: Boolean
        get() = System.getProperty("os.name").lowercase().contains("mac")

    /**
     * Return true if java is running in a unix like operating system.
     */
    val isUnix: Boolean
        get() = System.getProperty("os.name").lowercase().contains("windows") == false

    /**
     * Add or get log message.
     * Use empty string to clear log.
     */
    var logMessage: String
        get() {
            synchronized(messages) {
                return messages.joinToString("\n")
            }
        }

        set(value) {
            synchronized(messages) {
                if (value != "") {
                    messages.add(TimeFormat.LONG_TIME.format(milliSeconds = System.currentTimeMillis() - startTime, timeZone = "UTC") + "| $value")
                }
                else {
                    messages.clear()
                }
            }
        }

    /**
     * Run first in main method to change look and feel.
     * Valid themes strings are "native" and "nimbus"
     * Setup macOS stuff.
     */
    fun setup(theme: String = "", appName: String = "", aboutText: String = "", quitLambda: () -> Unit = {}) {
        try {
            if (isMac == true) {
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName)
                System.setProperty("apple.awt.application.name", appName)
                System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS")
                System.setProperty("apple.laf.useScreenMenuBar", "true")

                val className    = Class.forName("com.apple.eawt.Application")
                val application  = className.getMethod("getApplication")
                val instance     = application.invoke(null)
                val aboutHandler = Proxy.newProxyInstance(Class.forName("com.apple.eawt.AboutHandler").classLoader, arrayOf(Class.forName("com.apple.eawt.AboutHandler")), AboutHandler(appName, aboutText))
                val quitHandler  = Proxy.newProxyInstance(Class.forName("com.apple.eawt.QuitHandler").classLoader, arrayOf(Class.forName("com.apple.eawt.QuitHandler")), QuitHandler(quitLambda))

                instance.javaClass.getMethod("setAboutHandler", Class.forName("com.apple.eawt.AboutHandler")).invoke(instance,
                    aboutHandler
                )

                instance.javaClass.getMethod("setQuitHandler", Class.forName("com.apple.eawt.QuitHandler")).invoke(instance,
                    quitHandler
                )
            }
        }
        catch (e: Exception) {
            println(e.message)
        }

        try {
            when (theme) {
                "native" -> {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                }
                "nimbus" -> {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel")
                }
                else -> {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
                }
            }
        }
        catch (e: Exception) {
            println(e.message)
        }
    }
}
