/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Component
import java.awt.Dimension

/**
 * Resize widget
 */
fun Component.resize(x: Int, y: Int, w: Int, h: Int) {
    setLocation(x, y)
    setSize(w, h)
}

/**
 * Set Minimum size and preferred size
 * Set any value to -1 to skip that particular size
 */
fun Component.setMinAndPrefSize(minWidth: Int = -1, minHeight: Int = -1, preferedWidth: Int = -1, preferedHeight: Int = -1, maxWidth: Int = -1, maxHeight: Int = -1) {
    if (minWidth > 0 && minHeight > 0) {
        minimumSize = Dimension(minWidth, minHeight)
    }

    if (preferedWidth > 0 && preferedHeight > 0) {
        preferredSize = Dimension(preferedWidth, preferedHeight)
    }

    if (maxWidth > 0 && maxHeight > 0) {
        maximumSize = Dimension(maxWidth, maxHeight)
    }
}
