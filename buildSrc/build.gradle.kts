
val kotlin_version = "1.5.10"

repositories {
    google()
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

plugins {
    this.`kotlin-dsl`
//    id("org.gradle.kotlin.kotlin-dsl") version "7.0.2"
    this.`jvm-ecosystem`
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
    id("java-gradle-plugin")
//    id("kotlin-kapt")
}

//思路和booster一样 一个plugin一次文件复制，执行所有transform
//https://github.com/gradle/kotlin-dsl-samples
dependencies{
//    kapt("com.google.auto.service:auto-service:1.0")
//    kapt 'com.google.auto.service:auto-service:1.0-rc6'
    implementation(gradleApi())
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-plugins:2.1.6")
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:7.0.1")
    implementation("com.android.tools.build:gradle-api:7.0.1")
    implementation("commons-io:commons-io:2.10.0")
    implementation(kotlin("gradle-plugin", kotlin_version))
    implementation(kotlin("gradle-plugin-api", kotlin_version))
//    implementation("org.gradle.kotlin:kotlin-dsl:7.0.2")
}

//定义插件  就不需要 resources/META-INF/gradle-plugins/*.properties文件了
gradlePlugin {
    plugins {
        create("sparkj") {
            id = "jspark"
            implementationClass = "com.spark.FirstGPlugin"
        }
    }
}

//https://github.com/tschuchortdev/kotlin-compile-testing
//https://bnorm.medium.com/exploring-kotlin-ir-bed8df167c23

//完成以下功能
//https://github.com/zhuguohui/MehodInterceptor
//bytex
//huntter
//booster
