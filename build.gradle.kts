// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
val compose_version by extra("1.0.1")
    //    val kotlin_version = "1.5.21"
//    val compose_version = "1.0.0"
//    project.ext {
//        set("compose_version", "1.0.0")
//        set("kotlin_version", "1.5.21")
//    }
    println(project.extensions.getByName("ext"))
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.1")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
//task("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}
//task clean(type: Delete) {
//    rootProject.buildDir
//}

fun Project.dependenciesDef() = dependencies {
    "implementation"("androidx.core:core-ktx:1.6.0")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    "implementation"("androidx.appcompat:appcompat:1.3.1")
    "implementation"("com.google.android.material:material:1.4.0")
    "implementation"("androidx.compose.ui:ui:1.0.1")
    "implementation"("androidx.compose.material:material:$compose_version")
    "implementation"("androidx.compose.runtime:runtime:$compose_version")
    "implementation"("androidx.compose.animation:animation:$compose_version")
    "implementation"("androidx.compose.ui:ui-tooling-preview:$compose_version")
    "implementation"("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    "implementation"("androidx.activity:activity-compose:1.3.1")
    "testImplementation"("junit:junit:4.+")
    "androidTestImplementation"("androidx.test.ext:junit:1.1.3")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.4.0")
    "androidTestImplementation"("androidx.compose.ui:ui-test-junit4:1.0.1")
    "debugImplementation"("androidx.compose.ui:ui-tooling:$compose_version")
}

println(" ================================================= jspark >> $project ${project.ext} ${project.ext.properties}")