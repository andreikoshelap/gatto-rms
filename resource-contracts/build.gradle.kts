// resource-contracts/build.gradle.kts

plugins {
    `java-library`
    `maven-publish`
}

group = "com.gatto.rms"
version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.gatto.rms"
            artifactId = "resource-contracts"
            version = "1.0.0"
        }
    }
}

java {
    // Optional: ensure Java 21 toolchain
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories {
    mavenCentral()
}

dependencies {
    // Keep contracts lightweight; annotations are optional and compileOnly
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

}

tasks.test {
    useJUnitPlatform()
}


