dependencies {
    implementation(project(":exposed-springboot-starter"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.exposed:exposed-json")
    implementation("org.jetbrains.exposed:exposed-java-time")
    testImplementation(project(":exposed-springboot-test-starter"))
}
infra {
    useKspPlugin(project(":exposed-generator")) {
        arg("orm.springboot_aot", "true")
        arg("orm.table_group_id", project.group.toString())
        arg("orm.table_artifact_id", project.name)
    }
}


