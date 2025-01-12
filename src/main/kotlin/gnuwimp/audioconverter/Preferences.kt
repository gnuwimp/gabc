/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import java.io.File
import java.util.prefs.Preferences

//--------------------------------------------------------------------------
fun Preferences.getFile(entry: String, def: File): File {
    val file = File(entry)

    if (file.isDirectory == true) {
        return file
    }

    return def
}

//------------------------------------------------------------------------------
val Preferences.tab1DestFile: File
    get() = File(tab1DestPath)

//------------------------------------------------------------------------------
var Preferences.tab1DestPath: String
    get() = get("tab1_dest", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("tab1_dest", value)
    }

//------------------------------------------------------------------------------
val Preferences.tab1ImageFile: File
    get() = File(tab1ImagePath)

//------------------------------------------------------------------------------
var Preferences.tab1ImagePath: String
    get() = get("tab1_image", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        val f = File(value)

        if (f.isFile == true) {
            put("tab1_image", f.parentFile.canonicalPath)
        }
    }

//------------------------------------------------------------------------------
val Preferences.tab1SourceFile: File
    get() = File(tab1SourcePath)

//------------------------------------------------------------------------------
var Preferences.tab1SourcePath: String
    get() = get("tab1_source", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("tab1_source", value)
    }

//------------------------------------------------------------------------------
val Preferences.tab2DestFile: File
    get() = File(tab2DestPath)

//------------------------------------------------------------------------------
var Preferences.tab2DestPath: String
    get() = get("tab2_dest", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("tab2_dest", value)
    }

//------------------------------------------------------------------------------
val Preferences.tab2SourceFile: File
    get() = File(tab2SourcePath)

//------------------------------------------------------------------------------
var Preferences.tab2SourcePath: String
    get() = get("tab2_source", File(System.getProperty("user.home")).canonicalPath)

    set(value) {
        put("tab2_source", value)
    }

//------------------------------------------------------------------------------
var Preferences.winHeight: Int
    get() = getInt("win_height", 550)

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

