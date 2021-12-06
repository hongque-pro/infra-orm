dependencies {
    api("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.jetbrains.kotlin:kotlin-test-junit5")
    // https://mvnrepository.com/artifact/org.mockito/mockito-all
    api("com.h2database:h2")
    api(project(":exposed-starter"))
}

