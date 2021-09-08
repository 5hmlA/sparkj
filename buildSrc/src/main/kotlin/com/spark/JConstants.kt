package com.spark

import java.io.File

/**
 * @author yun.
 * @date 2021/9/3
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class JConstants {
    companion object {
        const val moduleClassName = "classes.jar"

        fun sout(msg : Any) {
            println(" $ Jspark > $msg")
        }
    }
}

inline fun String.isClass(): Boolean {
    return this.endsWith(".class")
}

inline fun String.isBuildConfigClass(): Boolean {
    return this.equals("BuildConfig.class")
}

inline fun String.isBindingClass(): Boolean {
    return this.endsWith("Binding.class")
}

inline fun String.isArouterClass(): Boolean {
    return this.startsWith("ARouter$$")
}

inline fun String.isRClass(): Boolean {
    return this.startsWith("R")
}

inline fun File.skipJar(): Boolean {
    return this.name.equals("R.jar")
}

inline fun File.skipFile(): Boolean {
    return !this.name.endsWith("class")
}

inline fun File.packageName(srcDirectory: File): String {
    val replace = this.path.replace(srcDirectory.path, "")
    return replace.substring(1).toPackageName()
}

inline fun String.toPackageName(): String {
    return substring(0, this.indexOf(".class")).replace("/|\\\\", ".")
}

inline fun String.skip(): Boolean {
    return !isClass()||isBuildConfigClass()
}

inline fun String.skipMore(): Boolean {
    return isBindingClass()||isArouterClass()
}

inline fun Any.sout() {
    println(" $ Jspark > $this")
}




