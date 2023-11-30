
infra {
    useKspApi()
}

dependencies {
    implementation(project(":exposed-core"))
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")

    testImplementation("dev.zacsweers.kctfork:ksp:${Versions.kotlinCompileTesting}")
}