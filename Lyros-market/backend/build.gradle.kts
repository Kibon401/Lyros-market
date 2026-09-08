plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.shadow.jar)
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

// Add this block to define where to download dependencies from
repositories {
    mavenCentral()
}

application {
    // Explicitly set the mainClass to your ApplicationKt where embeddedServer is defined
    mainClass.set("com.example.ApplicationKt") 
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Add this dependency for local development environment variables
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.config.yaml)

    // Ktor Client for M-Pesa
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.kotlinx.datetime)
    // implementation(libs.mysql.connector.java) // Swapped out for PostgreSQL
    implementation("org.postgresql:postgresql:42.6.0")
    implementation(libs.zaxxer.hikari)
    implementation(libs.jasypt.bcrypt)
    implementation(libs.logback.classic)
    
    // SMTP Mail
    implementation(libs.javax.mail)

    // Cloudinary for image uploads
    implementation(libs.cloudinary.http44)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
}