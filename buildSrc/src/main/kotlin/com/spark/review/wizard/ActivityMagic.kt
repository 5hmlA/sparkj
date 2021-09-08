package com.spark.review.wizard

import com.spark.JConstants
import org.objectweb.asm.*
import java.io.File
import java.util.jar.JarEntry

/**
 * @author yun.
 * @date 2021/7/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class ActivityMagic : IWizard() {

    override fun transformStart() {
        println(" ... $this >> transformStart ")
    }

    override fun checkIfJarMatches(srcJarFile: File, destJarFile: File): Boolean {
        return srcJarFile.name.contains(JConstants.moduleClassName)
    }

    override fun checkIfJarEntryMatches(srcJarEntry: JarEntry, srcJarFile: File, destJarFile: File): Boolean {
        return srcJarEntry.name.endsWith("Activity.class")
    }

    override fun transformJarEntry(classFileByte: ByteArray): ByteArray {
        return transformFile(classFileByte)
    }

    override fun checkIfFileMatches(srcFile: File, destFile: File, srcDirectory: File): Boolean {
        if (srcFile.name.endsWith("Activity.class")) {
            println(" ... $this >> checkIfFileMatches  ${srcFile.name} ")
            return true
        }
        return false
    }

    override fun transformFile(classFileByte: ByteArray): ByteArray {
        val classReader = ClassReader(classFileByte)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val lifecycleClassVisitor = LifecycleClassVisitor(classVisitor = classWriter)
        classReader.accept(lifecycleClassVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    override fun transformEnd() {
        println(" ... $this >> transformEnd ")
    }
}

class LifecycleClassVisitor(api: Int = Opcodes.ASM5, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor) {

    lateinit var className: String

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name!!
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println(" ... $this >> LifecycleClassVisitor --- visitMethod $name  $descriptor")
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name!! == "onCreate") {
            return LifecycleMethodVisitor(methodVisitor = visitMethod, className = className, methodName = name)
        }
        return visitMethod
    }
}

class LifecycleMethodVisitor(
    api: Int = Opcodes.ASM5, methodVisitor: MethodVisitor, val className: String, val methodName:
    String
) :
    MethodVisitor(
        api,
        methodVisitor
    ) {

    override fun visitCode() {
        super.visitCode()
        println(" ... $this >> LifecycleMethodVisitor -- visitCode ---")
        mv.visitLdcInsn("jspark")
        mv.visitLdcInsn("$className --> $methodName")
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false)
        mv.visitInsn(Opcodes.POP)
    }

}


