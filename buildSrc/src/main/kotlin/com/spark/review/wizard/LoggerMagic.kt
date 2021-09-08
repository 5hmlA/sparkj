package com.spark.review.wizard

import com.spark.sout
import java.io.File
import java.util.jar.JarEntry

/**
 * @author yun.
 * @date 2021/7/23
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class LoggerMagic : IWizard() {

    override fun transformStart() {
        " >>>>>>>>> transformStart ".sout()
    }

    override fun checkIfJarMatches(srcJarFile: File, destJarFile: File): Boolean {
        " >>>>>>>>> checkIfJarMatches ${srcJarFile.absolutePath}  > ${destJarFile.absolutePath}".sout()
        return true
    }

    override fun checkIfJarEntryMatches(srcJarEntry: JarEntry, srcJarFile: File, destJarFile: File): Boolean {
        " >>>>>>>>> checkIfJarEntryMatches ${srcJarEntry.name} ".sout()
        return true
    }

    override fun checkIfFileMatches(srcFile: File, destFile: File, srcDirectory: File): Boolean {
        " >>>>>>>>> checkIfFileMatches ${srcFile.absolutePath}  > ${destFile.absolutePath}".sout()
        return true
    }

    override fun transformEnd() {
        " >>>>>>>>> transformEnd ".sout()
    }
}