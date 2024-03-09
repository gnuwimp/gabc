/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.swing

import java.awt.Container
import java.awt.Font

/**
 * Set font for container and all its children recursive.
 */
var Container.fontForAll: Font
    //--------------------------------------------------------------------------
    get() {
        return this.font
    }

    //--------------------------------------------------------------------------
    set(value) {
        this.font = value

        this.components.forEach { component ->
            component?.font = value

            if (component is Container) {
                component.fontForAll = value
            }
        }
    }

