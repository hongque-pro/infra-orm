
infra {
    useKspApi()
    useKspApi("testImplementation")
}

kotlin {
    sourceSets {
        val test by getting {
            kotlin.srcDir("src/test/kotlin")
            kotlin.exclude("TestSource.kt")
        }
    }
}

dependencies {
    implementation(project(":exposed-core"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.exposed:exposed-java-time")
    implementation("org.jetbrains.exposed:exposed-json")
    implementation("org.springframework:spring-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json


    //https://github.com/zacsweers/kotlin-compile-testing
    testImplementation("dev.zacsweers.kctfork:ksp:${Versions.kotlinCompileTesting}")
    //testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    //testRuntimeOnly(files("${System.getProperty("java.home")}/../lib/tools.jar"))
}