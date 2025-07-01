plugins {
    id("org.graalvm.buildtools.native") version "0.10.+"
    id("org.springframework.boot") version "3.5.0"
}

graalvmNative {
    binaries.named("main") {
        sharedLibrary = false
        mainClass = "com.labijie.orm.dummy.AotTestApplicationKt"
    }

}

dependencies {
    implementation(project(":exposed-springboot-starter"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.exposed:exposed-json")
    implementation("org.jetbrains.exposed:exposed-java-time")
    implementation("com.h2database:h2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf")

    testImplementation(project(":exposed-springboot-test-starter"))
}
infra {
    useKotlinSerializationPlugin()
    useKspPlugin(project(":exposed-generator")) {
        arg("orm.springboot_aot", "true")
        arg("orm.pojo_kotlin_serializable", "true")
    }
}


