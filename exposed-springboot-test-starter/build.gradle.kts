
dependencies {
    api("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    // https://mvnrepository.com/artifact/org.mockito/mockito-all
    api("com.h2database:h2")
    api(project(":exposed-springboot-starter"))
}

