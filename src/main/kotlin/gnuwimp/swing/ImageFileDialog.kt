/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

/**
 * A photo preview for the dialog.
 */
private class PhotoPreview(fileChooser: JFileChooser) : JComponent() {
    private var thumbnail: ImageIcon? = null
    private var file:      File?      = null

    init {
        preferredSize = Dimension(200, 200)

        fileChooser.addPropertyChangeListener { pce ->
            var update = false
            val prop   = pce.propertyName

            if (JFileChooser.DIRECTORY_CHANGED_PROPERTY == prop) {
                file   = null
                update = true
            }
            else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY == prop) {
                file   = pce.newValue as File?
                update = true
            }

            if (update == true) {
                thumbnail = null

                if (isShowing) {
                    loadImage()
                    repaint()
                }
            }
        }
    }

    /**
     * Load image from disk.
     */
    private fun loadImage() {
        if (file != null) {
            try {
                val bufferedImage = ImageIO.read(file)

                if (bufferedImage != null) {
                    thumbnail = bufferedImage.toImageIcon(200.0)
                }
            }
            catch (e: Exception) {
            }
        }
    }

    /**
     * Draw image icon.
     */
    override fun paintComponent(graphics: Graphics) {
        if (thumbnail == null) {
            loadImage()
        }

        if (thumbnail != null) {
            val w = thumbnail?.iconWidth ?: 0
            val h = thumbnail?.iconHeight ?: 0
            var x = width / 2 - w / 2
            var y = height / 2 - h / 2

            if (y < 0) {
                y = 0
            }

            if (x < 5) {
                x = 5
            }

            thumbnail?.paintIcon(this, graphics, x, y)
        }
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * A file dialog for choosing an image.
 * File filter for the most used images is set.
 * And photo preview are turned on.
 */
class ImageFileDialog(val path: String, val parent: Component? = null) : JFileChooser() {
    init {
        val file = File(path)

        currentDirectory = if (file.isDirectory == false) {
            File(System.getProperty("user.home"))
        }
        else {
            file
        }

        fileFilter = object : FileFilter() {
            //------------------------------------------------------------------
            override fun getDescription() = "Image Files"

            //------------------------------------------------------------------
            override fun accept(file: File) = file.name.endsWith(".jpg") ||
                                              file.name.endsWith(".jpeg") ||
                                              file.name.endsWith(".gif") ||
                                              file.name.endsWith(".png") ||
                                              file.isDirectory
        }

        accessory  = PhotoPreview(this)
        fontForAll = Swing.defFont
    }

    /**
     * Show dialog and return a file  object or null.
     */
    val file: File?
        get() {
            return if (showOpenDialog(parent) == APPROVE_OPTION && selectedFile != null) {
                selectedFile
            }
            else {
                null
            }
        }
}
