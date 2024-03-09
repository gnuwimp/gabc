/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Container
import java.awt.Font

/**
 * Set font for container and all its children recursive.
 */
fun Container.setFontForAll(font: Font) {
    this.font = font

    this.components.forEach {
        it?.font = font

        if (it is Container) {
            it.setFontForAll(font)
        }
    }
}
