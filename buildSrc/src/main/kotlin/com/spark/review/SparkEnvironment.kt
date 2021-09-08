package com.spark.review

import com.android.build.api.transform.TransformInvocation
import org.gradle.api.Project

/**
 * @author yun.
 * @date 2021/9/8
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class SparkEnvironment() {
    companion object{
        val environment = SparkEnvironment()
    }
    var transformInvocation: TransformInvocation? = null
    var transformRootPath: String? = null
    var project: Project? = null

    fun release() {
        project = null
        transformInvocation = null
    }
}
