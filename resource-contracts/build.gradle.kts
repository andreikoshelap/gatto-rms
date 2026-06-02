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
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
}

repositories {
    mavenCentral()
}

dependencies {
    // Keep contracts lightweight; annotations are optional and compileOnly
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

}

tasks.test {
    useJUnitPlatform()
}

