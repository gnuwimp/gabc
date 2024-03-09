/*
 * Copyright 2016 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.ImageIcon

/**
 * Scale image icon and return new icon
 */
fun ImageIcon.scale(maxSize: Double): ImageIcon {
    return if (iconWidth >= iconHeight) {
        val scale = (maxSize / (iconWidth.toDouble() / iconHeight.toDouble())).toInt()
        ImageIcon(image.getScaledInstance(maxSize.toInt(), scale, Image.SCALE_SMOOTH))
    }
    else {
        val scale = (maxSize / (iconHeight.toDouble() / iconWidth.toDouble())).toInt()
        ImageIcon(image.getScaledInstance(scale, maxSize.toInt(), Image.SCALE_SMOOTH))
    }

}

/**
 * Scale image and return an icon
 */
fun BufferedImage.toImageIcon(maxSize: Double): ImageIcon {
    return if (width >= height) {
        val scale = (maxSize / (width.toDouble() / height.toDouble())).toInt()
        ImageIcon(getScaledInstance(maxSize.toInt(), scale, Image.SCALE_SMOOTH))
    }
    else {
        val scale = (maxSize / (height.toDouble() / width.toDouble())).toInt()
        ImageIcon(getScaledInstance(scale, maxSize.toInt(), Image.SCALE_SMOOTH))
    }

}

