

plugins {
    kotlin("jvm") version Versions.kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version Versions.kotlinVersion apply false
    id("io.github.gradle-nexus.publish-plugin") version Versions.publishingPluginVersion
}



val pom = PomInfo(
    description = "Orm and tooling for kotlin based exposed",
    projectUrl = "https://github.com/hongque-pro/infra-orm",
    gitUrl = "https://github.com/hongque-pro/infra-bom.git",
    githubScmUrl = "git@github.com:hongque-pro/infra-bom.git",
)

applyDefaultForAll(includeSource = true)
applyPublishingForAll(pom)

subprojects {
    group = "com.labijie.orm"
    version = "1.0.0"

    dependencies {
        implementation(platform("org.jetbrains.exposed:exposed-bom:${Versions.exposedVersion}"))
    }
}