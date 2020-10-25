import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
}
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}