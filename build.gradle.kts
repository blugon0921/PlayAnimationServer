import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val buildPath = File("C:/Files/Minecraft/Servers/\$PlayAnimation")
val kotlinVersion = kotlin.coreLibrariesVersion

repositories {
    mavenCentral()
    maven("https://repo.blugon.kr/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(files("minestom-dev.jar")) //Tickrate 60 minestom
//    implementation("net.minestom:minestom-snapshots:277dceacf1")
//    implementation("com.github.Project-Cepi:KStom:Tag")
    implementation("kr.blugon:node-fs:latest.release")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    // Core dependencies
    implementation(libs.slf4j)
    implementation(libs.jetbrainsAnnotations)
    implementation(libs.bundles.adventure)
    implementation(libs.minestomData)

    // Performance/data structures
    implementation(libs.caffeine)
    implementation(libs.fastutil)
    implementation(libs.bundles.flare)
    implementation(libs.gson)
    implementation(libs.jcTools)
}

extra.apply {
    set("ProjectName", project.name)
    set("ProjectVersion", project.version)
    set("KotlinVersion", kotlinVersion)
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

//    processResources {
//        filesMatching("*.yml") {
//            expand(project.properties)
//            expand(extra.properties)
//        }
//    }

    jar { this.build() }
    shadowJar { this.build() }
}

fun Jar.build() {
    val file = File("./build/libs/${project.name}.jar")
    if(file.exists()) file.delete()
    archiveBaseName.set(project.name) //Project Name
    archiveFileName.set("${project.name}.jar") //Build File Name
    archiveVersion.set(project.version.toString()) //Version
    from(sourceSets["main"].output)

    doLast {
        copy {
            from(archiveFile) //Copy from
            into(buildPath) //Copy to
        }
    }

    manifest {
//        attributes["Main-Class"] = "${project.group}.${project.name.lowercase()}.${project.name}Kt" //Main File
        attributes["Main-Class"] = "${project.group}.${project.name.lowercase()}.${project.name}Kt" //Main File
    }
}