/*
 * Copyright 2016 - 2025 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

/**
 * Thread to run Task objects
 */
class TaskThread(val task: Task) : Thread() {
    /**
     * Run task and set the status flag for the task object that it has been running
     * Exceptions are caught
     */
    override fun run() {
        try {
            task.status = Task.Status.RUNNING
            task.run()
            task.status = Task.Status.OK
        }
        catch (e: Exception) {
            task.status = Task.Status.ERROR
            task.error = e.message.toString()
        }
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * Base class for objects running in Progress
 * It will be executed in a thread
 * Max valuse has to be larger than 0
 */
abstract class Task(val max: Long = 100) {
    private var messageString = ""

    /**
     * Execution flags for Task object
     */
    enum class Status {
        WAITING, /** Task has not been started */
        RUNNING, /** Task is running */
        ERROR,   /** Task has stopped and it failed */
        OK       /** Task has stopped and it succeeded */
    }

    var abort = false
        internal set

    var connect: Task? = null
        internal set

    var error = ""
        internal set

    var message: String
        @Synchronized get() {
            return messageString
        }

        @Synchronized set(value) {
            messageString = value
        }

    var progress = 0L
        internal set

    var status = Status.WAITING
        internal set

    //--------------------------------------------------------------------------
    init {
        require(max > 0)
    }

    /**
     * Override to run code
     * Set error with an exception
     * If abort is true then throw an exception
     */
    abstract fun run()

    /**
     * Override to stop some nasty blocking
     * Stop is called before the task is joined or terminated
     */
    open fun stop() {
    }

    /**
     * Debug string
     */
    override fun toString() = "status=$status, max=${String.format("%6d", max)}, progress=${String.format("%6d", progress)}, error='$error'"
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * Return a list of objects that has failed
 */
fun List<Task>.allErrors(): List<Task> {
    return this.filter {
        it.status == Task.Status.ERROR
    }
}

/**
 * Return count of failed tasks
 */
val List<Task>.countError: Int
    get() = this.count{
        it.status == Task.Status.ERROR
    }

/**
 * Return count of succeeded tasks
 */
val List<Task>.countOk: Int
    get() = this.count{
        it.status == Task.Status.OK
    }

/**
 * Throw Exception on first error
 */
fun List<Task>.throwFirstError() {
    val err = this.find {
        it.error.isNotBlank()
    }?.error

    if (err != null) {
        throw Exception(err)
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

/**
 * A task manager object.
 * It uses Task objects to calculate progress values and execute task(s) in thread(s).
 * Thread count can't be more than task count.
 */
open class TaskManager(val tasks: List<Task>, val maxThreads: Int = 1, val onError: Execution = Execution.CONTINUE, val onCancel: Execution = Execution.STOP_JOIN) {
    var runningThreads = 0

    /**
     * Flags for Progress actions when running threads
     */
    enum class Execution {
        CONTINUE,       /** Continue if a task has an error */
        STOP_JOIN,      /** Stop all threads on error/cancel but wait for them */
        STOP_INTERRUPT  /** Stop all threads on error/cancel and interrupt them */
    }

    val total: Long
    private val threadList= mutableListOf<TaskThread?>()

    /**
     * Get number of active threads
     */
    val activeThreads: Int
        get() = runningThreads

    /**
     * Return percent value, between 0 and 100
     */
    val percent: Int
        get() = (progress.toDouble() / total.toDouble() * 100.0).toInt().coerceIn(0, 100)

    /**
     * Get current total value for all tasks (could be slow for lots of tasks, optimize?)
     */
    val progress: Long
        get() = tasks.sumByLong(Task::progress)

    //--------------------------------------------------------------------------
    init {
        require(maxThreads > 0 && tasks.size > 0)

        total = tasks.sumByLong(Task::max)

        for (f in 1..maxThreads) {
            threadList.add(null)
        }
    }

    /**
     * Get messages from tasks.
     */
    open fun message(threadCount: Int = 4): String {
        require(threadCount > 0)

        var res = ""
        var max = 0

        threadList.forEach { thread ->
            thread?.let {
                if (thread.state != Thread.State.TERMINATED) {
                    res += try {
                        thread.task.message
                    }
                    catch (e: Exception) {
                        e.message
                    }

                    res += "\n"
                    max++

                    if (max == threadCount) {
                        return res
                    }
                }
            }
        }

        return res
    }

    /**
     * Run all tasks in thread(s)
     * Returns false if it has stopped because of error or no more tasks to run
     */
    fun run(cancel: Boolean = false): Boolean {
        runningThreads = 0

        val failed = threadList.any {
            it?.task?.status == Task.Status.ERROR
        }

        if (failed == true && onError != Execution.CONTINUE) {
            stopThreads(onError)
            return false
        }
        else if (cancel == true) {
            stopThreads(onCancel)
            return false
        }
        else {
            for (i in threadList.indices) {
                if (threadList[i]?.state == Thread.State.TERMINATED) {
                    threadList[i] = null
                }

                if (threadList[i] == null) { // Empty thread slot found, now find new Task to run
                    for (f in tasks.indices) {
                        val task = tasks[f]

                        if (task.status == Task.Status.WAITING) { // Start new thread
                            val connect = task.connect

                            if (connect == null || connect.status == Task.Status.OK) {
                                task.status   = Task.Status.RUNNING
                                threadList[i] = TaskThread(task)
                                threadList[i]?.start()
                                break
                            }
                            else if (connect.status == Task.Status.ERROR) {
                                task.error  = "error: connected task has failed for this task"
                                task.status = Task.Status.ERROR
                            }
                        }
                    }
                }

                if (threadList[i] != null) {
                    runningThreads++
                }
            }

            return tasks.any {
                it.status == Task.Status.WAITING || it.status == Task.Status.RUNNING
            }
        }
    }

    /**
     * Stop threads
     */
    private fun stopThreads(stop: Execution) {
        require(stop == Execution.STOP_INTERRUPT || stop == Execution.STOP_JOIN)

        threadList.filterNotNull().forEach {
            it.task.abort = true
            it.task.stop()
        }

        Thread.sleep(1_000)

        for (i in 0 until maxThreads) {
            threadList[i]?.let { thread ->
                if (stop == Execution.STOP_JOIN) {
                    thread.join()
                }
                else {
                    thread.interrupt()
                }
            }

            threadList[i] = null
        }
    }

    /**
     * Debug string.
     */
    override fun toString() = "total=$total, progress=${String.format("%6d", progress)}, percent=${String.format("%3d", percent)}"
}
