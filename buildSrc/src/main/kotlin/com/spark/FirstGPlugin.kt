package com.spark

import com.spark.extension.AppleArg
import com.spark.extension.Argument
import com.spark.extension.GreetingPluginExtension
import com.spark.extension.Student
import com.spark.task.ExtensionTask
import com.spark.task.FirstTask
import com.spark.task.GreetingToFileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File

/**
 * @author yun.
 * @date 2021/7/19
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class FirstGPlugin : Plugin<Project> {

    //插件 apply 在哪里就是哪个project
    override fun apply(project: Project) {
        println("========= plugin =========")
        project.logger.lifecycle(" ==== lifecycle === ")
        project.logger.quiet(" ==== quiet === ")
        project.logger.debug(" ==== debug === ")
        project.logger.info(" ==== info === ")
        project.logger.warn(" ==== warn === ")
        project.logger.error(" ==== error === ")
        //<editor-fold desc="添加 extension">
        projectExtensionsLean(project)
        //</editor-fold>


        //<editor-fold desc="注册添加 task">
        projectTaskLearn(project)
        //</editor-fold>

//        projectMethod(project)

        //项目配置文件gradle解析结束
        project.afterEvaluate {
            println("========== afterEvaluate =========")
        }

        if (project.extensions.findByType(Argument::class.java)!!.buildTimeCheck) {
            BuildTimeCost().apply(project)
        }

        JsparkPlugin().apply(project)
    }

    private fun projectExtensionsLean(project: Project) {
        val extension = project.extensions.create("greeting", GreetingPluginExtension::class.java)
        val myArgument = project.extensions.create("myArg", Argument::class.java, project)
        project.extensions.add("apple", AppleArg::class.java)
        //创建的 Extension 对象都默认实现了 ExtensionAware 接口
        if (myArgument is ExtensionAware) {
            println(">>>>>>>>>>>>")
            //ExtensionAware 用于 创建嵌套 extensions 1
            myArgument.extensions.create("inner", AppleArg::class.java)
        }

        val container = project.container(Student::class.java)
        project.extensions.add("school", container)
        //        NamedDomainObjectContainer<Student> studentContainer = project.container(Student)
        //        project.extensions.add('team',studentContainer)
    }

    private fun projectTaskLearn(project: Project) {
        project.tasks.register("first", FirstTask::class.java)
        //        project.tasks.maybeCreate("first", FirstTask::class.java)
        //        project.tasks.create("first", FirstTask::class.java)
        project.tasks.register("extension", ExtensionTask::class.java)


        val greetingFile = File(project.buildDir.absolutePath + File.separator + "hell.txt")

        project.tasks.register("greet", GreetingToFileTask::class.java).also {
            it.get().destination = greetingFile
        }
        //        project.tasks.create("greet", GreetingToFileTask::class.java).also {
        //            it.destination
        //        }
        project.tasks.create("sayGreeting") {
            dependsOn("greet")
            doLast {
                println("${greetingFile.readText()} (file: ${greetingFile.name})")
            }
        }

        project.tasks.create("hello")
    }

    private fun projectMethod(project: Project) {
        println(" ============= projectMethod ================ ")
        //在 projectDir 下创建文件夹
        project.mkdir("${project.buildDir}/lalla")
        //在 projectDir 下创建文件
        project.file("${project.buildDir}/test").createNewFile()
        //fileTree列出指定文件夹下的所有文件
        project.fileTree("${project.buildDir}").onEach {
            println(it)
        }


//        val myCopySpec = project.copySpec {
//            it.into("webroot")
//            it.exclude("**/.data/**")
//            it.from("src/main/webapp") {
//                it.include("**/*.jsp")
//            }
//            it.from("src/main/js") {
//                it.include("**/*.js")
//            }
//        }
    }
}