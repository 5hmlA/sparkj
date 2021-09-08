package com.spark.review.wizard

import java.io.File
import java.util.jar.JarEntry

/**
 * @author yun.
 * @date 2021/7/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
abstract class IWizard {

    fun enable(): Boolean = true

    abstract fun transformStart()

    open fun checkIfJarMatches(srcJarFile: File, destJarFile: File): Boolean {
        return true
    }

    open fun checkIfJarEntryMatches(srcJarEntry: JarEntry, srcJarFile: File, destJarFile: File): Boolean {
        return true
    }

    open fun transformJarEntry(classFileByte: ByteArray): ByteArray {
        return classFileByte
    }

    open fun checkIfFileMatches(srcFile: File, destFile: File, srcDirectory: File): Boolean {
        return true
    }

    open fun transformFile(classFileByte: ByteArray): ByteArray {
        return classFileByte
    }

    abstract fun transformEnd()
}

