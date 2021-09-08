package com.spark

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.spark.review.wizard.ArouterMagic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.hasPlugin
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
        //监听每个task的执行
//        project.gradle.addListener(object : TaskExecutionListener {
//            override fun beforeExecute(p0: Task) {
//                println("*********** beforeExecute ${p0.path} **************")
//            }
//
//            override fun afterExecute(p0: Task, p1: TaskState) {
//                println("*********** afterExecute ${p0.path} **************")
//            }
//        })
        println(ArouterMagic.arouterApiJarPath)
        //gradle.properties是否存在
        if (project.rootProject.file("gradle.properties").exists()) {
            //gradle.properties文件->输入流
            val properties = Properties()
            project.rootProject.file("gradle.properties").inputStream().use {
                properties.load(it)
            }

            "false".toBoolean()
            println("read data from gradle.properties > ${properties.getProperty("android.useAndroidX", "false")}")
        }

        println("${project.name}  ${project.plugins.hasPlugin("com.android.application")}")
        println("${project.name}  ${project.plugins.hasPlugin(AppPlugin::class)}")

        println("${project.name}  ${project.extensions.findByName("android")}")
        println("${project.name}  ${project.extensions.getByName("android")}")
        //[DefaultTaskExecutionRequest{args=[assembleDebug],projectPath='null'}]
        //[DefaultTaskExecutionRequest{args=[clean, assembleRelease],projectPath='null'}]

        println(project.gradle.startParameter.taskRequests)
        val taskRequests = project.gradle.startParameter.taskRequests
        if (taskRequests.size > 0) {
            val args = taskRequests[0].args
            if (args.size > 0) {

                val predicate: (String) -> Boolean = { it.toLowerCase().contains("release") }
                if (args.any(predicate)) {
                    println("=================== release 启用ARouter自动注册 =====================")
                    val android = project.extensions.findByType<com.android.build.gradle.BaseExtension>()
                    println("project name: ${project.name}  $android  ${android?.transforms}")
                    android?.registerTransform(SparkTransform(project))
                }
            }
        }

//        val android = project.extensions.findByType(AppExtension::class.java)
        val android = project.extensions.findByType<AppExtension>()
        android?.registerTransform(SparkTransform(project))
        println("project name: ${project.name}  $android  ${android?.transforms}")
        //library中
//        val libraryExtension = project.extensions.findByType<LibraryExtension>()
//        libraryExtension?.registerTransform(MyTransform(project))
    }
}