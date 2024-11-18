val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val junit_version: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core and Features
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Database (Exposed, HikariCP, Postgres, H2)
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-json:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-money:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:$exposed_version")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.h2database:h2:2.3.232")

    // Scheduling
    implementation("org.quartz-scheduler:quartz:2.3.2")

    // JSON Parsing (Gson, Jackson)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    // Firebase
    implementation("com.google.firebase:firebase-admin:9.4.1")

    // Test Dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    testImplementation("org.junit.vintage:junit-vintage-engine:$junit_version")
}


sourceSets {
    test {
        java.srcDirs("src/test/kotlin")
    }
}

tasks.test {
    useJUnitPlatform()

    testClassesDirs = layout.buildDirectory.dir("classes/kotlin/test").get().asFileTree
    classpath = files(layout.buildDirectory.dir("classes/kotlin/test").get()) + classpath

    testLogging {
        events("passed", "failed", "skipped")
    }
}