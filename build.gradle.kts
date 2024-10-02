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

}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

mirai {
    jvmTarget = JavaVersion.VERSION_17
}
