import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    // Minecraft 26.1+ is distributed unobfuscated. Use Loom's non-remapping plugin.
    id("net.fabricmc.fabric-loom")
}

version = "${property("mod.version")}+${sc.current.version}"
group = property("maven.group") as String

base {
    archivesName = property("archives.base.name") as String
}

repositories {
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com")
    maven("https://maven.gegy.dev")
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("waylight") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")

    // No mappings dependency on 26.1+: Minecraft itself is unobfuscated.
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric")}")

    implementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }
    include("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")

    compileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    runtimeOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")

    implementation("dev.lambdaurora.lambdynamiclights:lambdynamiclights-runtime:${property("deps.lambdynamiclights")}")
}

tasks.named("check") {
    dependsOn(tasks.named("spotlessCheck"))
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.named<Jar>("jar") {
    from("LICENSE") {
        rename { "${it}_${property("archives.base.name")}" }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    // Non-obfuscated Loom uses the normal jar task; there is no remapJar step.
    from(tasks.named("jar").map { (it as Jar).archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs/fabric"))
    dependsOn("build")
}
