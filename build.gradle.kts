plugins {
    `java-library`
    id("io.github.goooler.shadow") version "8.1.8"
}

allprojects {
    group = "net.kkah"
    version = "1.0.0"

    apply(plugin = "java")
    apply(plugin = "io.github.goooler.shadow")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}