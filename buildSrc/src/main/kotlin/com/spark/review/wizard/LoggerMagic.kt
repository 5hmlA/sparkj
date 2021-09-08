package com.spark.review.wizard

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
        println(" >>>>>>>>> transformStart ")
    }

    override fun checkIfJarMatches(srcJarFile: File, destJarFile: File): Boolean {
        println(" >>>>>>>>> checkHandleWithJar ${srcJarFile.absolutePath}  > ${destJarFile.absolutePath}")
        return true
    }

    override fun checkIfJarEntryMatches(srcJarEntry: JarEntry, srcJarFile: File, destJarFile: File): Boolean {
        println(" >>>>>>>>> checkHandleWithJar ${srcJarEntry.name} ")
        return true
    }

    override fun checkIfFileMatches(srcFile: File, destFile: File): Boolean {
        println(" >>>>>>>>> checkHandleWithFile ${srcFile.absolutePath}  > ${destFile.absolutePath}")
        return true
    }

    override fun transformEnd() {
        println(" >>>>>>>>> transformEnd ")
    }
}