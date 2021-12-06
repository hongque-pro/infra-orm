

plugins {
    kotlin("jvm") version Versions.kotlinVersion
}



val pom = PomInfo(
    description = "Orm and tooling for kotlin based exposed",
    projectUrl = "https://github.com/hongque-pro/infra-orm",
    gitUrl = "https://github.com/hongque-pro/infra-orm.git",
    githubScmUrl = "git@github.com:hongque-pro/infra-orm.git",
)

useNexusPublishPlugin()

allprojects {
    group = "com.labijie.orm"
    version = "1.0.0"
    useDefault()
    dependencies {
        implementation(platform("org.jetbrains.exposed:exposed-bom:${Versions.exposedVersion}"))
    }
    if(!this.name.startsWith("dummy")){
        this.usePublishing(pom)
    }
}

