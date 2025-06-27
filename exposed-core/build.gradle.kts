

infra {
    useKotlinSerializationPlugin()
}

dependencies {
    api("org.jetbrains.exposed:exposed-core")
    compileOnly("org.graalvm.nativeimage:svm:${Versions.graalvmSvm}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf")
}