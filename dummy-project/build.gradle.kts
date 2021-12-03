plugins {
    id("com.google.devtools.ksp") version Versions.kspVersion
}

dependencies {
    implementation(project(":exposed-starter"))
    implementation(project(":exposed-generator"))
    ksp(project(":exposed-generator"))
}

ksp {
    arg("project", project.name)
    arg("projectDir", project.projectDir.absolutePath)
    arg("packageName", "com.labijie.orm.dummy.pojo")
}