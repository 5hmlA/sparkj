import org.gradle.api.Project

/**
 * <pre>
 *     author: dhl
 *     date  : 2020/7/3
 *     desc  : 配置和 Build 相关的
 * </pre>
 */
object BuildConfig {
    const val compileSdkVersion = 30
    const val buildToolsVersion = "29.0.3"
    const val minSdkVersion = 22
    const val targetSdkVersion = 30
    const val versionCode = 10001
    const val versionName = "1.0.1"
}

//fun Project.configureAndroid() = this.extensions.getByType(com.android.build.gradle.BaseExtension::class.java).run{
//    this.compileSdkVersion(30)
//}