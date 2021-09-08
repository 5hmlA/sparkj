package com.spark.review.wizard

import java.io.File

/**
 * @author yun.
 * @date 2021/9/8
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
class ClickMagic: IWizard() {
    override fun transformStart() {
    }

    override fun checkIfFileMatches(srcFile: File, destFile: File, srcDirectory: File): Boolean {
        return super.checkIfFileMatches(srcFile, destFile, srcDirectory)
    }

    override fun transformFile(classFileByte: ByteArray): ByteArray {

        return super.transformFile(classFileByte)
    }

    override fun transformEnd() {
    }
}