package com.spark

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.spark.review.Reviewer
import org.gradle.api.Project
import java.io.File
import org.apache.commons.io.FileUtils
import java.util.concurrent.TimeUnit
import com.spark.review.SparkEnvironment

/**
 * @author yun.
 * @date 2021/7/20
 * @des [用户自定义的Transform，会比系统的Transform先执行]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class SparkTransform constructor(project: Project) : Transform() {
    init {
        SparkEnvironment.environment.project = project
    }


//    用户自定义的Transform，会比系统的Transform先执行

    private val reviewer = Reviewer()

    override fun getName() = "JsparkTransform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    //    EXTERNAL_LIBRARIES：只有外部库
//    PROJECT：只有项目内容
//    PROJECT_LOCAL_DEPS：只有项目的本地依赖(本地jar)
//    PROVIDED_ONLY：只提供本地或远程依赖项
//    SUB_PROJECTS：只有子项目
//    SUB_PROJECTS_LOCAL_DEPS：只有子项目的本地依赖项(本地jar)
//    TESTED_CODE：由当前变量(包括依赖项)测试的代码
    override fun getScopes(): MutableSet<QualifiedContent.ScopeType> = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental() = true

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        SparkEnvironment.environment.transformInvocation = transformInvocation
        println("=============================================")
        println(transformInvocation.context.variantName)
        val nanoStartTime = System.nanoTime()

        reviewer.gerrit()

        " # $this == ${transformInvocation.isIncremental} ---- ${transformInvocation.context.variantName}".sout()
        if (!transformInvocation.isIncremental) {
            //不是增量编译，则清空output目录
            transformInvocation.outputProvider.deleteAll()
        }
//        增量编译，则要检查每个文件的Status，Status分为四种，并且对四种文件的操作不尽相同
//        NOTCHANGED 当前文件不需要处理，甚至复制操作都不用
//        ADDED、CHANGED 正常处理，输出给下一个任务
//        REMOVED 移除outputProvider获取路径对应的文件
        transformInvocation.inputs.onEach {
            "=============== transformInvocation.inputs.onEach ==================== start".sout()
            reviewDirectory(it, transformInvocation)
            reviewJarFile(it, transformInvocation)
            "================ transformInvocation.inputs.onEach =================== end ".sout()
        }

        reviewer.merge()
        val cost = System.nanoTime() - nanoStartTime
        " # $this == cost:$cost > ${TimeUnit.NANOSECONDS.toSeconds(cost)}".sout()
        SparkEnvironment.environment.release()
    }

    private fun reviewJarFile(
        it: TransformInput,
        transformInvocation: TransformInvocation
    ) {
        it.jarInputs.onEach { jar ->
            //                JarInput：它代表着以jar包方式参与项目编译的所有本地jar包或远程jar包，可以借助于它来实现动态添加jar包操作。
            //这里包括子模块打包的class文件debug\classes.jar
            //目标文件都被用数字重命名了，只有第一个transform的文件没被重命名
            val destJarFile = transformInvocation.outputProvider.getContentLocation(
                jar.name, jar.contentTypes, jar.scopes,
                Format.JAR
            )
            " # $this ==  reviewJarFile ${jar.file} =======".sout()
            if (transformInvocation.isIncremental) {
                when (jar.status!!) {
                    Status.NOTCHANGED -> {
                    }
                    Status.ADDED, Status.CHANGED -> {
                        " # $this ==  jarInputs ${jar.status} : ${jar.file.path} =======".sout()
                        reviewer.reviewJar(jar.file, destJarFile)
                    }
                    Status.REMOVED -> {
                        " # $this ==  jarInputs ${jar.status} : ${jar.file.path} =======".sout()
                        if (destJarFile.exists()) {
                            FileUtils.deleteQuietly(destJarFile)
                        }
                    }
                }
            } else {
                reviewer.reviewJar(jar.file, destJarFile)
            }
        }
    }

    private fun reviewDirectory(
        it: TransformInput,
        transformInvocation: TransformInvocation
    ) {
        //input 是循环
        //可能出现 direct没有jar多，direct很多jar没有
        it.directoryInputs.onEach { dir ->
            //DirectoryInput：它代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件，可以借助于它来修改输出文件的目录结构、目标字节码文件。
            val destDirectory = transformInvocation.outputProvider.getContentLocation(
                dir.name, dir.contentTypes, dir.scopes,
                Format.DIRECTORY
            )
            val srcDirectory = dir.file
            " # $this ==  reviewDirectory ${srcDirectory.path} =======".sout()
            if (transformInvocation.isIncremental) {
                //https://juejin.cn/post/6916304559602139149
                //https://github.com/Leifzhang/AndroidAutoTrack
                dir.changedFiles.onEach { entry ->
                    " # $this ==  directoryInputs ${entry.value} : ${entry.key.path} =======".sout()
                    when (entry.value!!) {
                        Status.NOTCHANGED -> {
                        }
                        Status.ADDED, Status.CHANGED -> {
                            reviewer.reviewFile(entry.key, srcDirectory, destDirectory)
                        }
                        Status.REMOVED -> {
                            val path = entry.key.absolutePath.replace(srcDirectory.absolutePath, destDirectory.absolutePath)
                            val destFile = File(path)
                            if (destFile.exists()) {
                                FileUtils.deleteQuietly(destFile)
                            }
                        }
                    }
                }
            } else {
                srcDirectory.walk().filter { it.isFile }.forEach {
                    reviewer.reviewFile(it, srcDirectory, destDirectory)
                }
            }
        }
    }

}