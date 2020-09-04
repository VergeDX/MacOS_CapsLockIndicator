@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"

    // https://github.com/spotbugs/spotbugs
    id("com.github.spotbugs") version "4.5.0"

    // https://github.com/ben-manes/gradle-versions-plugin
    id("com.github.ben-manes.versions") version "0.29.0"
}

group = "org.hydev"
version = "1.1"

repositories { mavenCentral() }
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    // https://github.com/kristian/system-hook
    implementation("com.1stleg:jnativehook:2.1.0")

    // https://github.com/zeroturnaround/zt-exec
    implementation("org.zeroturnaround:zt-exec:1.12")
}

// https://dev.to//preslavrachev/create-executable-kotlin-jars-using-gradle
// https://www.cnblogs.com/mengdd/p/android-gradle-migrate-from-groovy-to-kotlin.html
// https://gist.github.com/domnikl/c19c7385927a7bef7217aa036a71d807
apply(plugin = "application")
val jar by tasks.getting(Jar::class) {
    // Modify main class here!
    manifest { attributes["Main-Class"] = "org.hydev.MainKt" }

    // https://stackoverflow.com/questions/46157338/using-gradle-to-build-a-jar-with-dependencies-with-kotlin-dsl
    configurations["compileClasspath"].forEach { from(zipTree(it.absoluteFile)) }
}
