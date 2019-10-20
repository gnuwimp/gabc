/*
 * Copyright 2016 - 2019 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.gabc

import java.io.File
import java.util.prefs.Preferences

/**
 * Destination directory
 */
var Preferences.destPath: String
    get() = get("path_dest", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_dest", value)
    }

/**
 * Cover inage directory
 */
var Preferences.imagePath: String
    get() = get("path_pic", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_pic", value)
    }

/**
 * Source directory
 */
var Preferences.sourcePath: String
    get() = get("path_source", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_source", value)
    }

/**
 * Window height
 */
var Preferences.winHeight: Int
    get() = getInt("win_height", 340)

    set(value) {
        putInt("win_height", value)
    }

/**
 * Is window maximized
 */
var Preferences.winMax: Boolean
    get() = getBoolean("win_max", false)

    set(value) {
        putBoolean("win_max", value)
    }

/**
 * Window width
 */
var Preferences.winWidth: Int
    get() = getInt("win_width", 800)

    set(value) {
        putInt("win_width", value)
    }

/**
 * Window x pos
 */
var Preferences.winX: Int
    get() = getInt("win_x", 50)

    set(value) {
        putInt("win_x", value)
    }

/**
 * Window y pos
 */
var Preferences.winY: Int
    get() = getInt("win_y", 50)

    set(value) {
        putInt("win_y", value)
    }

