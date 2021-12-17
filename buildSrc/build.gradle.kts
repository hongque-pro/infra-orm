
plugins {
    `kotlin-dsl`
}

fun getProxyMavenRepository(): String {
    val proxy: String? = System.getenv("MAVEN_PROXY")?.ifBlank { null }
    return proxy ?: "https://maven.aliyun.com/nexus/content/groups/public/"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven {
        this.setUrl(getProxyMavenRepository())
        this.isAllowInsecureProtocol = true
    }
    mavenCentral()
}

dependencies {
    val kotlinVersion = "1.6.0"

    api("io.github.gradle-nexus:publish-plugin:1.1.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
}