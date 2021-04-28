/*
 * Copyright 2016 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

/**
 * Add numbers in collection and return it as an Long
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
 * Append strings in an list to this list
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
