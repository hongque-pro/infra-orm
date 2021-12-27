plugins {
    id("com.labijie.infra") version (Versions.infraPluginVersion) apply false
}

allprojects {
    group = "com.labijie.orm"
    version = "1.0.6"

    infra {
        useDefault {
            includeSource = true
            infraBomVersion = Versions.infraBomVersion
            kotlinVersion = Versions.kotlinVersion
            useMavenProxy = false

            addHongQueGitHubPackages()
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

            useGitHubPackages("hongque-pro", "infra-orm")
        }
    }

    dependencies {
        add("api", platform("org.jetbrains.exposed:exposed-bom:${Versions.exposedVersion}"))
    }
}