
dependencies {
    api(project(":exposed-core"))
    api("org.jetbrains.exposed:exposed-migration")
    api("org.jetbrains.exposed:spring-transaction")
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
//    implementation("cglib:cglib:${Versions.cglib}")
    testImplementation(project(":exposed-springboot-test-starter"))

}