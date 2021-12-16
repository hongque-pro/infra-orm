plugins {
    id("com.labijie.infra") version (Versions.infraPluginVersion) apply false
}

allprojects {
    group = "com.labijie.orm"
    version = "1.0.2"

    infra {
        useDefault {
            includeSource = true
            infraBomVersion = Versions.infraBomVersion
            kotlinVersion = Versions.kotlinVersion
            useMavenProxy = false
        }

        useNexusPublish()
    }
}

subprojects {
    infra {
        if (!project.name.startsWith("dummy")) {
            usePublish {
                description = "Orm and tooling for kotlin based exposed"
                githubUrl("hongque-pro", "infra-orm")
            }

        }
    }

    dependencies {
        add("api", platform("org.jetbrains.exposed:exposed-bom:${Versions.exposedVersion}"))
    }
}


//val pom = PomInfo(
//    description = "Orm and tooling for kotlin based exposed",
//    projectUrl = "https://github.com/hongque-pro/infra-orm",
//    gitUrl = "https://github.com/hongque-pro/infra-orm.git",
//    githubScmUrl = "git@github.com:hongque-pro/infra-orm.git",
//)
//
//useNexusPublishPlugin()
//
//allprojects {
//    group = "com.labijie.orm"
//    version = "1.0.0"
//    useDefault()
//    dependencies {
//        implementation(platform("org.jetbrains.exposed:exposed-bom:${Versions.exposedVersion}"))
//    }
//}
//
//subprojects {
//    if(!this.name.startsWith("dummy")){
//        this.usePublishing(pom)
//    }
//}
