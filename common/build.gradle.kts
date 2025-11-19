plugins {
    `java-library`
}

dependencies {
    // "api" exposes these dependencies to projects that depend on ":common"
    api("com.zaxxer:HikariCP:5.1.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    api("io.lettuce:lettuce-core:6.3.2.RELEASE")
}