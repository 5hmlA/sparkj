package com.spark.review

import com.spark.skipJar
import org.apache.commons.io.FileUtils
import java.util.jar.JarFile
import com.spark.review.wizard.ActivityMagic
import com.spark.review.wizard.ActivityMagic2
import com.spark.review.wizard.ArouterMagic
import com.spark.review.wizard.IWizard
import com.spark.skipFile
import com.spark.sout
import com.spark.work.Schedulers
import java.io.File
import java.util.*

/**
 * @author yun.
 * @date 2021/9/2
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */

interface IReview {
    fun gerrit()

    fun reviewFile(srcFile: File, srcDirectory: File, destDirectory: File)

    fun reviewJar(srcJarFile: File, destJarFile: File)

    fun merge()
}

class Reviewer : IReview {
    //        val transformCases = mutableListOf<ITransformCase>(ActivityTransform())
//    private val transformCases = mutableListOf<ITransformCase>(LoggerTransform())

    //        val transformCases = mutableListOf<ITransformCase>(ActivityTransform(), ArouterTransform())
//        val transformCases = mutableListOf<ITransformCase>(ActivityTransform(), ArouterTransform(), LoggerTransform(), ActivityTransform())
    val wizards = mutableListOf<IWizard>(
        ActivityMagic2(), ActivityMagic(),
        ArouterMagic()
    )

    init {
        //利用autoService自动发现服务
        ServiceLoader.load(IWizard::class.java).iterator().asSequence().filter { it.enable() }.forEach {
            wizards.add(it)
        }
    }


    private val scheduler = Schedulers()

    override fun gerrit() {
        wizards.forEach { case ->
            case.transformStart()
        }
    }

    override fun reviewFile(
        srcFile: File,
        srcDirectory: File,
        destDirectory: File
    ) {
        scheduler.submit {
            val destFilePath = srcFile.absolutePath.replace(srcDirectory.absolutePath, destDirectory.absolutePath)
            "reviewFile  ${srcFile.path} >> $destFilePath".sout()
            val destfile = File(destFilePath)
            // reviewFile  build\tmp\kotlin-classes\taining\META-INF\apptest_taining.kotlin_module >> intermediates\transforms\JsparkTransform\taining\76\META-INF\apptest_taining.kotlin_module
            val matchedWizards = if (srcFile.skipFile()) {
                Collections.emptyList<IWizard>()
            } else {
                wizards.filter { it.checkIfFileMatches(srcFile, destfile, srcDirectory) }
            }
            if (matchedWizards.isNotEmpty()) {
                ">>> reviewFile srcDirectory : ${srcDirectory.path} >> transform size : $matchedWizards".sout()
                srcFile.review(destfile) { bytes ->
                    matchedWizards.fold(bytes) { acc, wizard ->
                        wizard.transformFile(acc)
                    }
                }
            } else {
                FileUtils.copyFile(srcFile, destfile)
            }
        }
    }

    override fun reviewJar(srcJarFile: File, destJarFile: File) {
        scheduler.submit {
            FileUtils.touch(destJarFile)

            val matchedWizards = if (srcJarFile.skipJar()) {
                //过滤R.jar
                Collections.emptyList<IWizard>()
            } else {
                wizards.filter { it.checkIfJarMatches(srcJarFile, destJarFile) }
            }
//            val matchedWizards = wizards.filter { it.checkIfJarMatches(srcJarFile, destJarFile) }
            "reviewJar  ${srcJarFile.path} >> ${destJarFile.path}".sout()
            if (matchedWizards.isNotEmpty()) {
                //transformJar
                ">>> reviewJar  ${srcJarFile.path} >> transform size : $matchedWizards".sout()
//                if (srcJarFile.isRjar())
                JarFile(srcJarFile).review(destJarFile) { jarEntry, bytes ->
                    matchedWizards.filter { it.checkIfJarEntryMatches(jarEntry, srcJarFile, destJarFile) }
                        .fold(bytes) { acc, wizard ->
                            wizard.transformJarEntry(acc)
                        }
                }
            } else {
                FileUtils.copyFile(srcJarFile, destJarFile)
            }
        }
    }

    override fun merge() {
        scheduler.await()
        wizards.forEach { case ->
            case.transformEnd()
        }
    }
}