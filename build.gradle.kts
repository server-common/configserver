import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.25"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"

    id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "seg.work"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val springCloudVersion = "2024.0.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "amazoncorretto:17-alpine-jdk"
    }
    to {
        image = System.getenv("JIB_IMAGE")
        tags = setOf("latest")
        auth {
            username = System.getenv("TOKEN_USER")
            password = System.getenv("TOKEN_PWD")
        }
    }
    container {
        jvmFlags = listOf(
            "-Djasypt.encryptor.password=\${JASYPT_ENCRYPTOR_PASSWORD}",
        )
        ports = listOf("50050")
    }
}
