plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "com.plugin"
version = "0.1.0"

dependencies{
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation ("org.yaml:snakeyaml:2.2")
    implementation ("cn.hutool:hutool-all:5.8.28")
    implementation("cn.bigmodel.openapi:oapi-java-sdk:release-V4-2.0.2")
    implementation ("com.aliyun:ocr_api20210707:3.1.1")
    implementation ("junit:junit:4.13.2")
    implementation ("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation ("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("top.mrxiaom.mirai:overflow-core:0.9.9.515-f8d867b-SNAPSHOT")


}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

mirai {
    noTestCore = true
    setupConsoleTestRuntime {
        // 移除 mirai-core 依赖
        classpath = classpath.filter {
            !it.nameWithoutExtension.startsWith("mirai-core-jvm")
        }
    }
    jvmTarget = JavaVersion.VERSION_17
}