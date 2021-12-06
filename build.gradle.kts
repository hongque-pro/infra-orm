

plugins {
    kotlin("jvm") version Versions.kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version Versions.kotlinVersion apply false
    id("io.github.gradle-nexus.publish-plugin") version Versions.publishingPluginVersion
}



val pom = PomInfo(
    description = "Orm and tooling for kotlin based exposed",
    projectUrl = "https://github.com/hongque-pro/infra-orm",
    gitUrl = "https://github.com/hongque-pro/infra-orm.git",
    githubScmUrl = "git@github.com:hongque-pro/infra-orm.git",
)


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


nexusPublishing {
    repositories {
        sonatype {
            val u = project.getPropertyOrCmdArgs("PUB_USER", "u")
            val p = project.getPropertyOrCmdArgs("PUB_PWD", "p")
            if (u != null) {
                username.set(u)
                if (p != null) {
                    password.set(p)
                }
            }
        }
    }
}