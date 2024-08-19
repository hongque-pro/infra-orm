dependencies {
    implementation(project(":exposed-springboot-starter"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.exposed:exposed-json")
    implementation("org.jetbrains.exposed:exposed-java-time")
}
infra {
    useKspPlugin(project(":exposed-generator"))

}

