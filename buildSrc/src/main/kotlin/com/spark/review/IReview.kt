package com.spark.review

import org.apache.commons.io.FileUtils
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import com.spark.review.wizard.ActivityMagic
import com.spark.review.wizard.ActivityMagic2
import com.spark.review.wizard.ArouterMagic
import com.spark.review.wizard.IWizard
import com.spark.work.Schedulers
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
//            val destFilePath = srcFile.absolutePath.subSequence(srcDirectory.absolutePath, destDirectory.absolutePath)
            val destfile = File(destFilePath)
            FileUtils.touch(destfile)
            var inputStream: InputStream? = null
            var classFileByte: ByteArray? = null

            wizards.forEachIndexed { index, wizard ->
                if (wizard.checkIfFileMatches(srcFile, destfile)) {
                    println(">>> reviewFile  $wizard => $srcFile > $destFilePath")
                    if (inputStream == null) {
                        inputStream = srcFile.inputStream()
                        classFileByte = inputStream!!.readBytes()
                    }
                    classFileByte = wizard.transformFile(classFileByte!!)
                }
            }

            if (classFileByte == null) {
                FileUtils.copyFile(srcFile, destfile)
            } else {
                destfile.outputStream().use { output ->
                    output.write(classFileByte!!)
                }
            }
        }
    }

    override fun reviewJar(srcJarFile: File, destJarFile: File) {
        scheduler.submit {
            FileUtils.touch(destJarFile)
            val wantHandle = wizards.filter { it.checkIfJarMatches(srcJarFile, destJarFile) }
            if (wantHandle.isNotEmpty()) {
                //transformJar
                println(">>> reviewJar  $srcJarFile >> transform size : $wizards")
                val destjarOutputStream = JarOutputStream(FileOutputStream(destJarFile))
                destjarOutputStream.use {
                    val jarFile = JarFile(srcJarFile)
                    jarFile.use {
                        jarFile.entries().toList().forEach {
                            destjarOutputStream.putNextEntry(ZipEntry(it.name))
                            jarFile.getInputStream(it).use { inputStream ->
                                var classFileByte: ByteArray = IOUtils.toByteArray(inputStream)
                                wantHandle.forEachIndexed { index, wizard ->
                                    if (wizard.checkIfJarEntryMatches(it, srcJarFile, destJarFile)) {
                                        println(">>> reviewJar  $wizard => ${it.name}")
                                        classFileByte = wizard.transformJarEntry(classFileByte)
                                    }

                                }
                                destjarOutputStream.write(classFileByte)
                                destjarOutputStream.closeEntry()
                            }

                        }
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