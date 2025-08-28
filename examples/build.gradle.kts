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
    
    // DateTime for examples
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${datetimeVersion}")
}

// Configure the application plugin
application {
    mainClass.set("org.ollamakt.examples.BasicChatKt") // Default main class
}

// Allow switching between different example main classes
tasks.register<JavaExec>("runBasicChat") {
    group = "examples"
    description = "Run BasicChat example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.BasicChatKt")
}

tasks.register<JavaExec>("runStreaming") {
    group = "examples"
    description = "Run StreamingExample"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.StreamingExampleKt")
}

tasks.register<JavaExec>("runToolCalling") {
    group = "examples"
    description = "Run ToolCallingExample"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.ToolCallingExampleKt")
}

tasks.register<JavaExec>("runHttpClient") {
    group = "examples"
    description = "Run HTTP Client example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.HttpClientExampleKt")
}

tasks.register<JavaExec>("runChat") {
    group = "examples"
    description = "Run Chat API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.ChatExampleKt")
}

tasks.register<JavaExec>("runGenerate") {
    group = "examples"
    description = "Run Generate API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.GenerateExampleKt")
}

tasks.register<JavaExec>("runList") {
    group = "examples"
    description = "Run List models API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.ListExampleKt")
}

tasks.register<JavaExec>("runShow") {
    group = "examples"
    description = "Run Show model info API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.ShowExampleKt")
}

tasks.register<JavaExec>("runPull") {
    group = "examples"
    description = "Run Pull model API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.PullExampleKt")
}

tasks.register<JavaExec>("runCreate") {
    group = "examples"
    description = "Run Create model API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.CreateExampleKt")
}

tasks.register<JavaExec>("runDelete") {
    group = "examples"
    description = "Run Delete model API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.DeleteExampleKt")
}

tasks.register<JavaExec>("runCopy") {
    group = "examples"
    description = "Run Copy model API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.CopyExampleKt")
}

tasks.register<JavaExec>("runEmbed") {
    group = "examples"
    description = "Run Embed API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.EmbedExampleKt")
}

tasks.register<JavaExec>("runPs") {
    group = "examples"
    description = "Run PS (running processes) API example"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.PsExampleKt")
}

tasks.register<JavaExec>("runChatNoThink") {
    group = "examples"
    description = "Run Chat API example with thinking disabled"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.ollamakt.examples.ChatNoThinkExampleKt")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}