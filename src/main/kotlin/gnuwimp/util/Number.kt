/*
 * Copyright © 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.abs

/**
 * Convert double number to string with thousandSeperator for every thousand but not for the decimals
 */
fun Double.format(thousandSeperator: String, numDecimals: Int, stringWidth: Int = 0): String {
    require(stringWidth >= 0)

    val rnum = abs(this) - abs(this).toLong()
    val left = this.toLong().format(thousandSeperator, stringWidth)

    val right = when (numDecimals) {
        0 -> return left
        1 -> String.format(Locale.US, "%.1f", rnum)
        2 -> String.format(Locale.US, "%.2f", rnum)
        3 -> String.format(Locale.US, "%.3f", rnum)
        4 -> String.format(Locale.US, "%.4f", rnum)
        5 -> String.format(Locale.US, "%.5f", rnum)
        6 -> String.format(Locale.US, "%.6f", rnum)
        7 -> String.format(Locale.US, "%.7f", rnum)
        8 -> String.format(Locale.US, "%.8f", rnum)
        else -> throw Exception("error: numDecimals is out of range")
    }

    return left + right.substring(startIndex = 1)
}

/**
 * Convert integer to byte array
 */
fun Int.toByteArray(littleEndian: Boolean = true): ByteArray {
    val bf = ByteBuffer.allocate(4)

    if (littleEndian == true) {
        bf.order(ByteOrder.LITTLE_ENDIAN)
    }
    else {
        bf.order(ByteOrder.BIG_ENDIAN)
    }

    bf.putInt(this)
    return bf.array()
}

/**
 * Convert integer to string with thousandSeperator for every thousand
 */
fun Long.format(thousandSeperator: String, stringWidth: Int = 0): String {
    require(stringWidth >= 0)

    var res = ""

    for ((i, c) in "$this".reversed().withIndex()) {
        if (i > 0 && i % 3 == 0 && c in '0'..'9') {
            res += thousandSeperator
        }

        res += c
    }

    res = res.reversed()

    if (stringWidth > res.length) {
        val form1 = "%%%ds"
        val form2 = form1.format(stringWidth)
        return form2.format(res)
    }

    return res
}
