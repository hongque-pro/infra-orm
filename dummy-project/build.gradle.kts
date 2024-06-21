dependencies {
    implementation(project(":exposed-springboot-starter"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
}
infra {
    useKspPlugin(project(":exposed-generator"))

}

