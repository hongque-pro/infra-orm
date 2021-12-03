
dependencies {
    implementation(gradleApi())
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.kspVersion}")
    implementation("org.jetbrains.exposed:spring-transaction")
    implementation("com.squareup:kotlinpoet-ksp:${Versions.kotlinPoetVersion}")
    api ("com.fasterxml.jackson.module:jackson-module-kotlin"){
        exclude(module = "slf4j-log4j12")
        exclude(module = "kotlin-reflect")
    }
}