package com.spark

/**
 * @author yun.
 * @date 2021/9/3
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class Constants {
    companion object {
        const val moduleClassName = "classes.jar"

    }
}

fun String.isClass(): Boolean {
    return this.endsWith(".class")
}

fun String.isRClass(): Boolean {
    return this.endsWith("R.class")
}

fun String.isBuildConfigClass(): Boolean {
    return this.endsWith("BuildConfig.class")
}


