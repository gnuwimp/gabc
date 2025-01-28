/*
 * Copyright 2021 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.audioconverter

import gnuwimp.util.Task
import gnuwimp.util.TaskManager

//------------------------------------------------------------------------------
class ConvertManager(tasks: List<Task>, maxThreads: Int = 1, onError: Execution = Execution.CONTINUE, onCancel: Execution = Execution.STOP_JOIN) : TaskManager(tasks, maxThreads, onError, onCancel) {
    companion object {
        private var countDecoded  = 0L

        //----------------------------------------------------------------------
        @Synchronized fun add(value: Long) {
            countDecoded += value
        }

        //----------------------------------------------------------------------
        @Synchronized fun clear() {
            countDecoded  = 0
        }
    }

    //--------------------------------------------------------------------------
    override fun message(threadCount: Int): String {
        var decoded = if (countDecoded < 1_000_000_000) "Decoded total ${countDecoded / 1_000_000} MB" else "Decoded total %.2f GB".format(countDecoded.toFloat() / 1_000_000_000.0)
        decoded += "\n"
        decoded += super.message(threadCount)
        return decoded
    }
}
