plugins {
    id("com.google.devtools.ksp") version Versions.kspVersion
}

dependencies {
    implementation(project(":exposed-starter"))
    implementation(project(":exposed-generator"))
    ksp(project(":exposed-generator"))
}

ksp {
    arg("exg_packageName", "com.labijie.orm.dummy.pojo")
}