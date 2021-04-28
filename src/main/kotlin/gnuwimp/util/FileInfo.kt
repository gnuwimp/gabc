/*
 * Copyright 2020 - 2021 gnuwimp@gmail.com
 * Released under the GNU General Public License v3.0
 */

package gnuwimp.util

import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.FileVisitOption.FOLLOW_LINKS
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.DosFileAttributes
import java.nio.file.attribute.FileTime
import java.nio.file.attribute.PosixFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * An file information object.
 * Create an object with filename and it will load all stat from the file.
 */
class FileInfo(pathname: String) {
    /**
     * Options for reading files from a directory.
     */
    enum class ReadDirOption {
        FILES_ONLY_IN_START_DIRECTORY,  /** Only files in root directory */
        ALL_IN_START_DIRECTORY,         /** Files and directories in root directory */
        ALL_RECURSIVE,                  /** Files and directories in all child directories */
        ALL_RECURSIVE_AND_DIRLINKS,     /** Files and directories in all child directories including links to directories*/
    }

    /**
     * File types
     */
    enum class Type {
        MISSING,  /** Missing file or invalid link */
        FILE,     /** File (can be an link) */
        DIR,      /** Directory (can be an link) */
        OTHER,    /** Something else (only on unix) */
    }

    var filepath: Path? = null;                   private set
    var ext             = "";                     private set
    var filename        = "";                     private set
    var iso             = "";                     private set
    var isLink          = false;                  private set
    var liso            = "";                     private set
    var mod: FileTime   = FileTime.fromMillis(0); private set
    var name            = "";                     private set
    var path            = "";                     private set
    var realname        = "";                     private set
    var size            = 0L;                     private set
    var type            = Type.MISSING;           private set
    var canRead         = false;                  private set
    var canWrite        = false;                  private set
    var canExecute      = false;                  private set

    /**
     * Create object from filename string.
     * Most exceptions are caught.
     */
    init {
        val file = File(pathname)

        filename   = file.absolutePath
        name       = file.name
        ext        = file.extension
        canRead    = file.canRead()
        canWrite   = file.canWrite()
        canExecute = file.canExecute()

        filepath = try {
            FileSystems.getDefault().getPath(filename)
        }
        catch (e: Exception) {
            null
        }

        try {
            val tmp = filepath

            if (tmp != null) {
                path     = tmp.parent.toAbsolutePath().toString()
                realname = tmp.toRealPath().toString()
                isLink   =  try {
                    Files.readSymbolicLink(tmp)
                    true
                }
                catch (e: Exception) {
                    false
                }

                val attr1 = if (isUnix == true) Files.readAttributes(tmp, PosixFileAttributes::class.java) else Files.readAttributes(tmp, DosFileAttributes::class.java)

                if (attr1 != null) {
                    val tmpMod = attr1.lastModifiedTime()

                    if (isUnix == false && isLink == false) {
                        isLink = try {
                            val attr2 = Files.readAttributes(tmp, BasicFileAttributes::class.java, LinkOption.NOFOLLOW_LINKS)
                            attr2.isDirectory == true && attr2.isOther == true
                        }
                        catch (e: Exception) {
                            isLink
                        }
                    }

                    size = attr1.size()
                    iso  = extractDate(tmpMod.toString())
                    liso = extractDate(LocalDateTime.ofInstant(tmpMod.toInstant(), ZoneId.systemDefault()).toString())
                    mod  = tmpMod
                    type = when {
                        attr1.isRegularFile -> Type.FILE
                        attr1.isDirectory -> Type.DIR
                        attr1.isOther -> Type.OTHER
                        else -> Type.MISSING
                    }
                }
            }
        }
        catch (e: Exception) {
            type = Type.MISSING
        }
    }

    /**
     * Get canonical path.
     */
    val canonicalPath: String
        get() = try {
            file.canonicalPath
        }
        catch (e: Exception) {
            ""
        }

    /**
     *
     */
    fun copyTo(to: FileInfo): Boolean {
        return try {
            val f = file
            val d = to.file

            f.copyTo(target = d, overwrite = true)
            d.setLastModified(mod.toMillis())
            d.setExecutable(canExecute)
            d.setReadable(canRead)
            d.setWritable(canWrite)
            true
        }
        catch (e: Exception) {
            false
        }
    }

    /**
     * Get File object from this filename.
     */
    val file: File = File(filename)

    /**
     * Is this an directory (can be an link).
     */
    val isCircular: Boolean
        get() {
            val name = realname + File.separator
            return isLink == true && filename.indexOf(name) == 0
        }

    /**
     * Is this an directory (can be an link).
     */
    val isDir: Boolean = type == Type.DIR

    /**
     *  Is this an regular file (can be an link).
     */
    val isFile: Boolean = type == Type.FILE

    /**
     * Is file missing, can be an invalid link if file has been created by readDir.
     */
    val isMissing: Boolean = type == Type.MISSING

    /**
     * Type string.
     */
    val typeString: String
        get() = if (type == Type.FILE && isLink == true) {
            "File Link"
        }
        else if (type == Type.FILE) {
            "File"
        }
        else if (type == Type.DIR && isCircular == true) {
            "Dir C.Link"
        }
        else if (type == Type.DIR && isLink == true) {
            "Dir Link"
        }
        else if (type == Type.DIR) {
            "Dir"
        }
        else if (type == Type.OTHER) {
            "Other"
        }
        else {
            "Missing"
        }

    /**
     * Read files in this directory.
     */
    fun readDir(option: ReadDirOption = ReadDirOption.ALL_IN_START_DIRECTORY): List<FileInfo> {
        val path = filepath
        return if (path != null) {
            val list = mutableListOf<FileInfo>()
            readDir(list, path, option)
            list
        }
        else {
            mutableListOf()
        }
    }

    /**
     * Remove a file or directory (with option to do delete all child files/directories).
     */
    fun remove(recursive: Boolean = false, checkExist: Boolean = true): Boolean {
        return try {
            if (isDir == true && recursive == true) {
                readDir(ReadDirOption.ALL_RECURSIVE).asReversed().forEach {
                    it.file.remove(checkExist = checkExist)
                }

                file.remove(checkExist = checkExist)
            }
            else {
                file.remove(checkExist = checkExist)
            }
        }
        catch (e: Exception) {
            false
        }
    }

    /**
     * Debug string.
     */
    fun toShortString(): String {
        val f = String.format("%-70s", filename)
        val l = if (liso == "") "0000-00-00 00:00:00" else liso
        return "$f  ${size.format("'", 10)}  $l  $typeString"
    }

    /**
     * Debug string.
     */
    override fun toString(): String {
        return "FileInfo(filename=$filename, path=$path, name=$name, size=$size, iso=$iso, liso=$liso, mod=${mod.to(TimeUnit.SECONDS)} type=$typeString)"
    }

    companion object {
        /**
         * Return true path is case sensitive.
         * It has to write to an file to test it
         */
        fun isCaseSensitive(path: String): Boolean {
            var res   = false
            val file1 = path + File.separator + "072d397af0cd4a8da11fbd5da4a27db4"
            val file2 = path + File.separator + "072D397AF0CD4A8DA11FBD5DA4A27DB4"

            try {
                File(file1).writeText("Hello World")
                res = File(file2).isFile == false
            }
            catch (e: Exception) {
            }
            finally {
                try {
                    File(file1).delete()
                }
                catch (e: Exception) {
                }

                return res
            }
        }

        /**
         * Return true path is case sensitive (Not reliable).
         */
        val isCaseSensitive: Boolean
            get() = File("a") != File("A")

        /**
         * Return true if java is running in an unix like operating system.
         */
        val isUnix: Boolean
            get() = System.getProperty("os.name").toLowerCase().contains("windows") == false

        //--------------------------------------------------------------------------
        private fun extractDate(date: String): String {
            var res = ""

            if (date.length > 9) {
                res += date.substring(0, 10)
                res += " "
            }

            if (date.length > 18) {
                res += date.substring(11, 19)
            }
            else if (date.length > 15) {
                res += date.substring(11, 16)
                res += ":00"
            }

            return res
        }

        /*
         * Callback for file visitor.
         */
        class FileVisitor(val path: Path, val files: MutableList<FileInfo>, val option: ReadDirOption) : SimpleFileVisitor<Path>() {
            //------------------------------------------------------------------
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                if (path != dir) {
                    val fi = readDirAddFile(files, dir, option)

                    if (fi != null) {
                        if (fi.isLink == true && option == ReadDirOption.ALL_RECURSIVE) {
                            return FileVisitResult.SKIP_SUBTREE
                        }
                        else if (fi.isCircular == true && option == ReadDirOption.ALL_RECURSIVE_AND_DIRLINKS) {
                            return FileVisitResult.SKIP_SUBTREE
                        }
                    }
                }

                return super.preVisitDirectory(dir, attrs)
            }

            //------------------------------------------------------------------
            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                readDirAddFile(files, file, option)
                return super.visitFile(file, attrs)
            }

            //------------------------------------------------------------------
            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
                readDirAddFile(files, file, option)
                return FileVisitResult.SKIP_SUBTREE
            }

            //------------------------------------------------------------------
            override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                return super.postVisitDirectory(dir, exc)
            }
        }

        //--------------------------------------------------------------------------
        private fun readDir(files: MutableList<FileInfo>, path: Path, option: ReadDirOption) {
            val visitor  = FileVisitor(path, files, option)
            val set      = mutableSetOf<FileVisitOption>()
            val maxDepth = if (option == ReadDirOption.ALL_IN_START_DIRECTORY || option == ReadDirOption.FILES_ONLY_IN_START_DIRECTORY) 1 else Int.MAX_VALUE

            if (option == ReadDirOption.ALL_RECURSIVE_AND_DIRLINKS) {
                set.add(FOLLOW_LINKS)
            }

            Files.walkFileTree(path, set, maxDepth, visitor)
        }

        //--------------------------------------------------------------------------
        private fun readDirAddFile(files: MutableList<FileInfo>, file: Path?, option: ReadDirOption): FileInfo? {
            if (file != null) {
                val fi = FileInfo(file.toString())

                if ((fi.isFile == true || fi.isMissing == true) && option == ReadDirOption.FILES_ONLY_IN_START_DIRECTORY) {
                    files.add(fi)
                }
                else if (option != ReadDirOption.FILES_ONLY_IN_START_DIRECTORY) {
                    files.add(fi)
                }

                return fi
            }
            else {
                return null
            }
        }
    }
}
