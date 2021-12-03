
plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "2.1.7"
    id("org.gradle.kotlin.embedded-kotlin") version "2.1.7"
}

fun getProxyMavenRepository(): String {
    val proxy: String? = System.getenv("MAVEN_PROXY")?.ifBlank { null }
    return proxy ?: "https://maven.aliyun.com/nexus/content/groups/public/"
}

repositories {
    mavenLocal()
    maven {
        this.setUrl(getProxyMavenRepository())
        this.isAllowInsecureProtocol = true
    }
    mavenCentral()
}