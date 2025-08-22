val ktorVersion: String by project
val coroutineVersion: String by project
val serializationVersion: String by project
val datetimeVersion: String by project

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Depend on the lib module
    implementation(project(":lib"))
    
    // Add kotlinx serialization for JSON examples
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
    
    // Coroutines for async examples
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}")
}

// Configure the application plugin
application {
    mainClass.set("BasicChatKt") // Default main class
}

// Allow switching between different example main classes
tasks.register<JavaExec>("runBasicChat") {
    group = "examples"
    description = "Run BasicChat example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("BasicChatKt")
}

tasks.register<JavaExec>("runStreaming") {
    group = "examples"
    description = "Run StreamingExample"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("StreamingExampleKt")
}

tasks.register<JavaExec>("runToolCalling") {
    group = "examples"
    description = "Run ToolCallingExample"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("ToolCallingExampleKt")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}