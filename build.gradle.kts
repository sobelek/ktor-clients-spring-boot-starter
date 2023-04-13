import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("maven-publish")
    id("java-library")
    id("signing")
}

group = "io.github.sobelek"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
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
configurations{
    create("exposedRuntime"){
        isCanBeConsumed=true
        isCanBeResolved=false
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
java{
    withJavadocJar()
    withSourcesJar()
}

publishing {

    publications {
        create<MavenPublication>("mavenJava") {
            pom{
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
            groupId = "io.github.sobelek"
            artifactId = "ktor-clients-spring-boot-starter"
            version = "0.0.1-SNAPSHOT"

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