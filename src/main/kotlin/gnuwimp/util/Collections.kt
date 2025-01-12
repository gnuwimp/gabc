/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

/**
 * Find string in array
 */
fun Array<String>.find(find: String): Int {
    var res = -1

    for (string in this) {
        res++

        if (string == find) {
            return res
        }
    }
    return -1
}

/**
 * Find double value in array
 */
fun Array<String>.findDouble(find: String, def: Double): Double {
    val num = this.findString(find, "")

    if (num == "") {
        return def
    }

    return try {
        num.toDouble()
    }
    catch (e: Exception) {
        def
    }
}

/**
 * Find long value in array
 */
fun Array<String>.findInt(find: String, def: Long): Long {
    val num = this.findString(find, "")

    if (num == "") {
        return def
    }

    return try {
        num.toLong()
    }
    catch (e: Exception) {
        def
    }
}

/**
 * Find string value in array
 */
fun Array<String>.findString(find: String, def: String): String {
    var res = false

    for (string in this) {
        if (res == true) {
            return string
        }
        else if (string == find) {
            res = true
        }
    }

    return def
}

/**
 * Add numbers in collection and return it as a Long
 */
fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L

    for (element in this) {
        sum += selector(element)
    }

    return sum
}

/**
 *
 * Add strings from a list to this list and return new list
 */
fun List<String>.add(strings: List<String>): List<String> {
    val list = this.toMutableList()

    for (s in strings) {
        list.add(s)
    }

    return list
}

/**
 * Join keys and return a string
 */
fun <K, V> Map<K, V>.joinKeys(stringSeperator: String = " "): String {
    var ret = ""

    for (it in this) {
        if (ret.isNotEmpty()) {
            ret += stringSeperator
        }

        ret += it.key.toString()
    }

    return ret
}


/**
 * Append strings in a list to this list
 */
fun MutableList<String>.append(strings: List<String>) {
    strings.forEach { this.add(it) }
}

/**
 * Swap two items in list
 * Indexes must be in range or exception will be throwed
 */
fun <T> MutableList<T>.swap(firstIndex: Int, secondIndex: Int) {
    require(firstIndex >= 0 && firstIndex < this.size && secondIndex >= 0 && secondIndex < this.size)

    val tmp      = this[firstIndex]
    this[firstIndex]  = this[secondIndex]
    this[secondIndex] = tmp
}
