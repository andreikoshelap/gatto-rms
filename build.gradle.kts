plugins {
    java
}

allprojects {
    group = "com.gatto"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    plugins.withType<JavaPlugin> {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }
}
