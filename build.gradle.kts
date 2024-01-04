val ktor_version = "1.5"
val kotlin_version = "1.8.0"
val logback_version = "1.4.2"

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.3.6"
}

group = "net.grandcentrix.backend"
version = "0.0.1"
application {
    mainClass.set("net.grandcentrix.backend.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-html-builder:$ktor_version")
    implementation ("org.apache.commons:commons-csv:1.10.0")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation(kotlin("test"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}