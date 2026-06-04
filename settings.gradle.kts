plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "resource-parent"
include("resource-service")


include("resource-publisher")
include("resource-consumer")
include("resource-contracts")
include("security-contracts")