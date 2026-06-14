plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.1"
}

group = "ru.deelter"
version = "1.0.0"
description = "Advanced shop system with flexible costs and products"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.15.7")
    compileOnly(files("libs/MultiEconomy-1.0.jar"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    // Отключаем стандартный jar, чтобы не создавал два файла
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.3")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }
}