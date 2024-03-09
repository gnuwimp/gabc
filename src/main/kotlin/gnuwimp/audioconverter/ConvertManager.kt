/*
 * Copyright 2021 - 2024 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.Task
import gnuwimp.util.TaskManager

//------------------------------------------------------------------------------
class ConvertManager(tasks: List<Task>, threadCount: Int = 1, onError: Execution = Execution.CONTINUE, onCancel: Execution = Execution.STOP_JOIN) : TaskManager(tasks, threadCount, onError, onCancel) {
    companion object {
        private var countDecoded  = 0L
        private var threadMessage = ""

        //----------------------------------------------------------------------
        @Synchronized fun add(value: Long) {
            countDecoded += value
        }

        //----------------------------------------------------------------------
        @Synchronized fun clear() {
            countDecoded  = 0
            threadMessage = ""
        }

        //----------------------------------------------------------------------
        var tm: String
            get() = "readonly"

            @Synchronized set (value) {
                threadMessage = value
            }
    }

    //--------------------------------------------------------------------------
    override fun message(threadCount: Int): String {
        val decoded = if (countDecoded < 1_000_000_000) "Decoded total ${countDecoded / 1_000_000} MB" else "Decoded total %.2f GB".format(countDecoded.toFloat() / 1_000_000_000.0)
        return "$decoded\n$threadMessage"
    }
}