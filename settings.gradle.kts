rootProject.name = "infra-orm"
include("exposed-core")
include("exposed-springboot-starter")
include("exposed-springboot-test-starter")
include("exposed-generator")
include("dummy-project")

pluginManagement {

    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
