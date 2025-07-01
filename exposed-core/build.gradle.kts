

infra {
    useKotlinSerializationPlugin()
}

dependencies {
    api("org.jetbrains.exposed:exposed-core") {
        exclude(group = "org.jetbrains.kotlin")
    }
    compileOnly("org.graalvm.nativeimage:svm:${Versions.graalvmSvm}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf")
}