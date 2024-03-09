/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import java.io.File
import java.util.prefs.Preferences

//------------------------------------------------------------------------------
var Preferences.destPath: String
    get() = get("path_dest", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_dest", value)
    }

//------------------------------------------------------------------------------
val Preferences.destPathFile: File
    get() = File(destPath)

//------------------------------------------------------------------------------
var Preferences.destPath2: String
    get() = get("path_dest2", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_dest2", value)
    }

//------------------------------------------------------------------------------
val Preferences.destPath2File: File
    get() = File(destPath2)

//------------------------------------------------------------------------------
var Preferences.imagePath: String
    get() = get("path_pic", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_pic", value)
    }

//------------------------------------------------------------------------------
var Preferences.sourcePath: String
    get() = get("path_source", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_source", value)
    }

//------------------------------------------------------------------------------
val Preferences.sourcePathFile: File
    get() = File(sourcePath)

//------------------------------------------------------------------------------
val Preferences.startPathFile: File
    get() = File(startPath)

//------------------------------------------------------------------------------
var Preferences.startPath: String
    get() = get("path_start", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("path_start", value)
    }

//------------------------------------------------------------------------------
var Preferences.winHeight: Int
    get() = getInt("win_height", 500)

    set(value) {
        putInt("win_height", value)
    }

//------------------------------------------------------------------------------
var Preferences.winMax: Boolean
    get() = getBoolean("win_max", false)

    set(value) {
        putBoolean("win_max", value)
    }

//------------------------------------------------------------------------------
var Preferences.winWidth: Int
    get() = getInt("win_width", 600)

    set(value) {
        putInt("win_width", value)
    }

//------------------------------------------------------------------------------
var Preferences.winX: Int
    get() = getInt("win_x", 50)

    set(value) {
        putInt("win_x", value)
    }

//------------------------------------------------------------------------------
var Preferences.winY: Int
    get() = getInt("win_y", 50)

    set(value) {
        putInt("win_y", value)
    }

