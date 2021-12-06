plugins {
    id("com.google.devtools.ksp") version Versions.kspVersion
}

dependencies {
    implementation(project(":exposed-starter"))
    implementation(project(":exposed-generator"))
    ksp(project(":exposed-generator"))
}
