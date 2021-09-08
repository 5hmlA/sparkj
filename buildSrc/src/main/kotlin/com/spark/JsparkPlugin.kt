package com.spark

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import java.util.*

/**
 * @author yun.
 * @date 2021/7/20
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class JsparkPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val taskReauests = project.gradle.startParameter.taskRequests
        taskReauests.sout()
        if (taskReauests.size > 0) {
            val args = taskReauests[0].args.filter { !it.equals("clean") }
            args.sout()
            if (args.isNotEmpty()) {
//                val predicate: (String) -> Boolean = { it.toLowerCase().contains("release") }
//                if (args.any(predicate)) {
//
//                }
                val android = project.extensions.findByType<com.android.build.gradle.BaseExtension>()
                "project name: ${project.name}  $android  ${android?.transforms}".sout()
                android?.registerTransform(SparkTransform(project))
            }
        }
    }
}