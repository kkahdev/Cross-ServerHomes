dependencies {
    implementation(project(":common"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-bukkit-shade:9.5.3")

    // Missing Caffeine dependency added here
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
}

tasks.shadowJar {
    relocate("dev.jorel.commandapi", "net.kkah.redishomes.shaded.commandapi")
    relocate("com.github.benmanes.caffeine", "net.kkah.redishomes.shaded.caffeine")
}