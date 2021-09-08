package com.spark.review

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import com.spark.skip
import com.spark.sout

/**
 * @author yun.
 * @date 2021/9/8
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
fun File.touch(): File {
    FileUtils.touch(this)
    return this
}

//处理class文件 从srcFile复制到destFile ，review这个过程，中间对byteArray做一次处理
inline fun File.review(destFile: File, wizard: (ByteArray) -> ByteArray) {
    destFile.touch().outputStream().use { outputStream ->
        this.inputStream().use {
            if (this.name.skip()) {
                outputStream.write(it.readBytes())
            } else {
                outputStream.write(it.review(wizard))
            }

        }
    }
}

/**
 * 处理jar 从srcFile复制到destFile ，review这个过程，中间 俺需要修改jar 对byteArray做一次处理
 * 1, 遍历jar中的jarEntry
 * 2, putNextEntry一个空的zipEntry对应jarEntry 到jaroutputstrean
 * 3，往jarOutputStream写入jarEntry的byre数据
 * 4，关闭closeEntry
 */
inline fun JarFile.review(destFile: File, jarWizard: (srcJarEntry: JarEntry, bytes: ByteArray) -> ByteArray) {
    //jarfile需要close
    this.use { jarFile ->
        JarOutputStream(destFile.touch().outputStream()).use { jarOutputStream: JarOutputStream ->
            jarFile.entries().asIterator().forEach { jarEntry ->
                "jarEntry.reivew > $jarEntry ".sout()
                //先添加entry
                jarOutputStream.putNextEntry(ZipEntry(jarEntry.name))
                //读取jarEntry中的流 进行处理
                jarFile.getInputStream(jarEntry).use { inputStream ->
                    if (jarEntry.name.skip()) {
                        jarOutputStream.write(inputStream.readBytes())
                    } else {
                        //写入到 目标jar流
                        jarOutputStream.write(jarWizard(jarEntry, inputStream.readBytes()))
                    }
                    //finish 一个jarEntry的写入
                    jarOutputStream.closeEntry()
                }
            }
        }
    }
}


/**
 * review InputStream 转换成byteArray 同时利用asm对byteArray做一些处理
 */
inline fun InputStream.review(wizard: (ByteArray) -> ByteArray = { it -> it }): ByteArray {
    return wizard(readBytes())
}