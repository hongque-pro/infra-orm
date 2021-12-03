
dependencies {
    implementation(project(":exposed-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.kspVersion}")
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")
}