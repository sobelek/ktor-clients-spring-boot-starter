import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("pl.allegro.tech.build.axion-release") version "1.15.0"
    id("maven-publish")
    id("java-library")
    id("signing")
}

group = "io.github.sobelek"

scmVersion {
    ignoreUncommittedChanges.set(false)
}

version = scmVersion.version
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
}
val bootJar: BootJar by tasks

bootJar.enabled = false

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    api("io.ktor:ktor-client-core:2.2.4")
    api("io.ktor:ktor-client-cio:2.2.4")
    api("io.ktor:ktor-client-logging-jvm:2.2.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

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
java{
    withJavadocJar()
    withSourcesJar()
}

publishing {

    publications {
        create<MavenPublication>("mavenJava") {
            version = scmVersion.version
            pom{
                name.set("ktor-clients-spring-boot-starter")
                description.set("Spring autoconfigure for ktor clients")
                url.set("https://github.com/sobelek/ktor-clients-spring-boot-starter")
                developers {
                    developer {
                        id.set("sobelek")
                        name.set("Jakub Sobczak")
                        email.set("ksobczak16@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:sobelek/ktor-clients-spring-boot-starter.git")
                    developerConnection.set("scm:git@github.com:sobelek/ktor-clients-spring-boot-starter.git")
                    url.set("https://github.com/sobelek/ktor-clients-spring-boot-starter")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
            from(components["java"])

        }
    }
    repositories {
        maven {
            name = "mavenCentralRepository"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials(PasswordCredentials::class)
        }
    }

}
val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

signing {
    setRequired{ isReleaseVersion && gradle.taskGraph.hasTask("publish") }
    sign(publishing.publications["mavenJava"])
}