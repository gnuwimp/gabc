/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import javax.imageio.ImageIO

/**
 *
 */
fun Closeable.safeClose(): Boolean {
    return try {
        this.close()
        true
    }
    catch(e: Exception) {
        false
    }
}

/**
 * Check if file is an image
 */
val File.isImage: Boolean
    get() {
        var ret = false

        try {
            if (isFile == true) {
                val bufferedImage: BufferedImage = ImageIO.read(this)

                if (bufferedImage.width > 0 && bufferedImage.height > 0) {
                    ret = true
                }
            }
        }
        catch (e: Exception) {
        }
        finally {
        }

        return ret
    }

/**
 * Delete file.
 */
fun File.remove(checkExist: Boolean = true): Boolean  {
    return if (checkExist == true && exists() == false) {
        false
    }
    else if (delete() == true) {
        true
    }
    else {
        if (this.canWrite() == false) {
            this.setWritable(true)
        }

        delete()
    }
}

/**
 *
 */
fun File.safeRemove(): Boolean {
    return try {
        return remove(false)
    }
    catch(e: Exception) {
        false
    }
}
