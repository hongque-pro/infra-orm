
infra {
    useKspApi()
}

dependencies {
    implementation(project(":exposed-core"))
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json


    //https://github.com/zacsweers/kotlin-compile-testing
    testImplementation("dev.zacsweers.kctfork:ksp:${Versions.kotlinCompileTesting}")
}