
infra {
    useKspApi()
}

dependencies {
    implementation(project(":exposed-core"))
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")
}