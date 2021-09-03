package com.spark.wizard

import com.android.build.gradle.internal.LoggerWrapper
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry

/**
 * @author yun.
 * @date 2021/7/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
abstract class IWizard {

    abstract fun transformStart()

    open fun checkIfJarMatches(srcJarFile: File, destJarFile: File): Boolean {
        return true
    }

    open fun checkIfJarEntryMatches(srcJarEntry: JarEntry, srcJarFile: File, destJarFile: File): Boolean {
        return true
    }

    open fun transformJar(inputStream: InputStream): ByteArray {
        return IOUtils.toByteArray(inputStream)
    }

    open fun checkIfFileMatches(srcFile: File, destFile: File): Boolean {
        return true
    }

    open fun transformFile(inputStream: InputStream): ByteArray {
        return IOUtils.toByteArray(inputStream)
    }

    abstract fun transformEnd()
}

