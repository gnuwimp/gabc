/*
 * Copyright 2016 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

/**
 * Xor two bytes
 */
fun Byte.xoor(xval: Byte): Byte = this.toInt().xor(xval.toInt()).toByte()

/**
 * Convert 4 bytes to Long
 */
fun ByteArray.getIntAt(index: Int = 0, smallEndian: Boolean = true): Long {
    return if (smallEndian) {
        ((this[index + 3].toLong() and 0xFF) shl 24) or
        ((this[index + 2].toLong() and 0xFF) shl 16) or
        ((this[index + 1].toLong() and 0xFF) shl 8) or
        (this[index + 0].toLong() and 0xFF)
    }
    else {
        ((this[index + 0].toLong() and 0xFF) shl 24) or
        ((this[index + 1].toLong() and 0xFF) shl 16) or
        ((this[index + 2].toLong() and 0xFF) shl 8) or
        (this[index + 3].toLong() and 0xFF)
    }
}

/**
 * Convert byte to unsigned value
 */
fun ByteArray.getUnsignedByteAt(index: Int = 0): Int {
    return this[index].toInt() and 0xFF
}
