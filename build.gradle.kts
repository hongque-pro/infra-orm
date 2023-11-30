
plugins {
    id("com.labijie.infra") version Versions.infraPluginVersion apply true
}


allprojects {
    group = "com.labijie.orm"
    version = "2.0.0"

    infra {
        useDefault {
            includeSource = true
            includeDocument = true
            useMavenProxy = false
        }
        usePublishPlugin()
    }
}

subprojects {
    infra {
        if (!project.name.startsWith("dummy")) {
            publishing {
                pom {
                    description = "Orm and tooling for kotlin based exposed"
                    githubUrl("hongque-pro", "infra-orm")
                }
                toGithubPackages("hongque-pro", "infra-orm")
            }
        }
    }

    dependencies {
        add("api", platform("org.jetbrains.exposed:exposed-bom:${Versions.exposedVersion}"))
    }
}