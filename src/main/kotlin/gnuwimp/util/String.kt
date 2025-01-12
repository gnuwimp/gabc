/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

import java.time.LocalDate
import java.util.*

/**
 * Align string in center position with spaces to the left and right
 */
fun String.alignCenter(width: Int): String {
    return if (this.length >= width) {
        this
    }
    else {
        val space1 = width - this.length
        val space2 = space1 / 2
        val space3 = width - (this.length + space2)
        val form1  = "%%-%ds"
        val form2  = form1.format(space2)
        val form3  = form1.format(space3)
        form2.format("") + this + form3.format("")
    }
}

/**
 * Align string to the left with spaces to the right
 */
fun String.alignLeft(width: Int): String {
    return if (this.length >= width) {
        this
    }
    else {
        val form1 = "%%-%ds"
        val form2 = form1.format(width)
        form2.format(this)
    }
}

/**
 * Align string to the right with spaces to the left
 */
fun String.alignRight(width: Int): String {
    return if (this.length >= width) {
        this
    }
    else {
        val form1 = "%%%ds"
        val form2 = form1.format(width)
        form2.format(this)
    }
}

/**
 * Breake string at certain intervals and return a List with String objects
 */
fun String.breakLine(breakPos: Int): List<String> {
    val list = mutableListOf<String>()

    if (this.length <= breakPos) {
        list.add(this)
        return list
    }

    for (f in this.indices step breakPos) {
        val end = f + breakPos

        if (end <= this.length) {
            list.add(this.substring(f, end))
        }
        else {
            list.add(this.substring(f, this.length))
        }
    }

    return list
}

/**
 * Capitalize words
 * Valid modes are "words", "lower", "upper"
 */
fun String.capWords(mode: String): String {
    var value = ""
    var flip  = true

    for (f in 0 until length) {
        var c = get(f)

        when (mode) {
            "words" -> {
                if (c.isLetter() == true && flip == true) {
                    flip = false
                    c = c.uppercaseChar()
                }
                else if (c.isLetter() == true)
                    c = c.lowercaseChar()
                else
                    flip = true
            }
            "lower" -> c = c.lowercaseChar()
            "upper" -> c = c.uppercaseChar()
        }

        value += c
    }

    return value
}

/**
 * Return string as a long or 0 if failed
 */
val String.numOrZero: Long
    get() {
        return try {
            this.toLong()
        }
        catch (e: Exception) {
            0
        }
    }

/**
 * Return string as a long or -1 if failed
 */
val String.numOrMinus: Long
    get() {
        return try {
            this.toLong()
        }
        catch (e: Exception) {
            -1
        }
    }

/**
 * Return a string without illegal file characters
 */
val String.removeIllegalFileChar: String
    get() = this.replace(Regex("[\\\\/:*?\"<>|.]"), "")

/**
 * Remove all leading char that are not letters
 */
val String.removeLeadingNoneLetters: String
    get() {
        var value = ""
        var eat   = false

        this.forEach { c ->
            if (c.isLetter() && !eat)
                eat = true

            if (eat)
                value += c
        }

        return value
    }

/**
 * Remove all trailing char that are not letters
 */
val String.removeTrailingNoneLetters: String
    get() = reversed().removeLeadingNoneLetters.reversed()

/**
 * Return string as formatted iso date "YYYY-MM-DD" or empty string if date can't be converted or validated
 * Current input formats is "YYYYMMDD", "YYYY-MM-DD", "MMDD", "MM-DD", "DD", ""
 * If date is a partial date it tries to add current year and/or month
 */
val String.toIsoDate: String
    get() {
        return try {
            when (length) {
                0 -> {
                    val ld = LocalDate.now()
                    String.format("%04d-%02d-%02d", ld.year, ld.monthValue, ld.dayOfMonth)
                }
                2 -> {
                    val ln = LocalDate.now()
                    val ld = LocalDate.of(ln.year, ln.monthValue, substring(0, 2).toInt())
                    String.format("%04d-%02d-%02d", ld.year, ld.monthValue, ld.dayOfMonth)
                }
                4 -> {
                    val ln = LocalDate.now()
                    val ld = LocalDate.of(ln.year, substring(0, 2).toInt(), substring(2, 4).toInt())
                    String.format("%04d-%02d-%02d", ld.year, ld.monthValue, ld.dayOfMonth)
                }
                5 -> {
                    val ln = LocalDate.now()
                    val ld = LocalDate.of(ln.year, substring(0, 2).toInt(), substring(3, 5).toInt())
                    String.format("%04d-%02d-%02d", ld.year, ld.monthValue, ld.dayOfMonth)
                }
                8 -> {
                    val ld = LocalDate.of(substring(0, 4).toInt(), substring(4, 6).toInt(), substring(6, 8).toInt())
                    String.format("%04d-%02d-%02d", ld.year, ld.monthValue, ld.dayOfMonth)
                }
                10 -> {
                    val ld = LocalDate.of(substring(0, 4).toInt(), substring(5, 7).toInt(), substring(8, 10).toInt())
                    String.format("%04d-%02d-%02d", ld.year, ld.monthValue, ld.dayOfMonth)
                }
                else -> ""
            }
        }
        catch (e: Exception) {
            return ""
        }
    }

/**
 * Return string as formatted iso time "hh:mm:ss" or empty string if time can't be converted
 * Current input formats is "hhmmss", "hh:mm:ss", "mmss", "mm:ss", "mm", ""
 */
val String.toIsoTime: String
    get() {
        return try {
            when (length) {
                0 -> "00:00:00"

                2 -> {
                    val mm = toInt()
                    if (mm in 0..59)
                        String.format("00:%02d:00", mm) else ""
                }

                4 -> {
                    val mm = substring(0, 2).toInt()
                    val ss = substring(2, 4).toInt()
                    if (mm in 0..59 && ss in 0..59)
                        String.format("00:%02d:%02d", mm, ss)
                    else
                        ""
                }

                5 -> {
                    val mm = substring(0, 2).toInt()
                    val ss = substring(3, 5).toInt()
                    if (mm in 0..59 && ss in 0..59)
                        String.format("00:%02d:%02d", mm, ss)
                    else
                        ""
                }

                6 -> {
                    val hh = substring(0, 2).toInt()
                    val mm = substring(2, 4).toInt()
                    val ss = substring(4, 6).toInt()
                    if (hh in 0..23 && mm in 0..59 && ss in 0..59)
                        String.format("%02d:%02d:%02d", hh, mm, ss)
                    else
                        ""
                }

                8 -> {
                    val hh = substring(0, 2).toInt()
                    val mm = substring(3, 5).toInt()
                    val ss = substring(6, 8).toInt()
                    if (hh in 0..23 && mm in 0..59 && ss in 0..59)
                        String.format("%02d:%02d:%02d", hh, mm, ss)
                    else
                        ""
                }

                else -> ""
            }
        }
        catch (e: Exception) {
            ""
        }
    }

/**
 * Remove all space, tab, newline and return char from the beginning and the end of the string
 */
val String.trimWhiteSpace: String
    get() = this.trim(' ', '\t', '\r', '\n')

/**
 * Time formats for strings
 */
enum class TimeFormat {
    ISO,                    /** "YYYYMMDD HHMMSS" */
    LONG_ISO,               /** "YYYY-MM-DD HH:MM:SS" */
    DATE,                   /** "YYYYMMDD" */
    LONG_DATE,              /** "YYYY-MM-DD" */
    TIME,                   /** "HHMMSS" */
    LONG_TIME,              /** "HH:MM:SS" */
    LONG_TIME_AND_MILLI,    /** "HH:MM:SS.mmm" */
    MINSEC,                 /** "MMSS" */
    LONG_MINSEC,            /** "MM:SS" */
    LONG_MINSEC_AND_MILLI,  /** "MM:SS.mmm" */
}

/**
 * Format number of milliseconds to an ISO kind of string
 */
fun TimeFormat.format(milliSeconds: Long, timeZone: String = ""): String {
    val date = Date(milliSeconds)
    val cal  = Calendar.getInstance()

    cal.time = date

    if (timeZone.isNotEmpty())
        cal.timeZone = TimeZone.getTimeZone(timeZone)

    return when (this) {
        TimeFormat.ISO -> String.format("%04d%02d%02d %02d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))
        TimeFormat.LONG_ISO -> String.format("%04d-%02d-%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))
        TimeFormat.DATE -> String.format("%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        TimeFormat.LONG_DATE -> String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        TimeFormat.TIME -> String.format("%02d%02d%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))
        TimeFormat.LONG_TIME -> String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))
        TimeFormat.LONG_TIME_AND_MILLI -> String.format("%02d:%02d:%02d.%03d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND))
        TimeFormat.MINSEC -> String.format("%02d%02d", cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))
        TimeFormat.LONG_MINSEC -> String.format("%02d:%02d", cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))
        TimeFormat.LONG_MINSEC_AND_MILLI -> String.format("%02d:%02d.%03d", cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND))
    }
}

