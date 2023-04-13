import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("maven-publish")
}

group = "github.sobelek"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
}

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

publishing {
    publications {
        create<MavenPublication>("maven-local") {
            groupId = "github.sobelek"
            artifactId = "ktor-clients-spring-boot-starter"
            version = "0.0.1-SNAPSHOT"

            from(components["java"])
        }
    }
}