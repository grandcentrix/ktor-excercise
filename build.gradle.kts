val ktor_version = "1.5"
val kotlin_version = "1.8.0"
val logback_version = "1.4.14"

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.3.6"
    kotlin("plugin.serialization") version "1.6.0"
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
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation ("org.apache.commons:commons-csv:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.0")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
