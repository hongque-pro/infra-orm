
infra {
    useKspApi()
}

dependencies {
    implementation(project(":exposed-core"))
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")

    //https://github.com/zacsweers/kotlin-compile-testing
    testImplementation("dev.zacsweers.kctfork:ksp:${Versions.kotlinCompileTesting}")
}