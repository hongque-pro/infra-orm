import com.sun.org.apache.bcel.internal.util.ClassPath
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension
import java.lang.IllegalArgumentException
import java.net.URL

fun Any?.isNotNullOrBlank(): Boolean {
    return !(this == null || this.toString().isBlank())
}

fun getProxyMavenRepository(): String {
    val proxy: String? = System.getenv("MAVEN_PROXY")?.ifBlank { null }
    return proxy ?: "https://maven.aliyun.com/nexus/content/groups/public/"
}

fun Project.canBeSign(): Boolean {
    val project = this
    return project.findProperty("signing.password").isNotNullOrBlank() &&
            project.findProperty("signing.secretKeyRingFile").isNotNullOrBlank() &&
            project.findProperty("signing.keyId").isNotNullOrBlank()
}

fun Project.getStringProperty(propertyName: String, defaultValue: String? = null): String? {
    val v = this.findProperty(propertyName)?.toString()
    return v ?: defaultValue
}

fun Project.getPropertyOrCmdArgs(propertyName: String, cmdArgName: String): String? {
    val project = this
    val propertyValue = project.getStringProperty(propertyName)
    return (System.getProperty(cmdArgName) ?: propertyValue) ?: System.getenv(propertyName)?.ifEmpty { null }
}

fun RepositoryHandler.useDefaultRepositories(useMavenProxy: Boolean = true) {
    mavenLocal()
    if (useMavenProxy) {
        maven {
            this.setUrl(getProxyMavenRepository())
            this.isAllowInsecureProtocol = true
        }
    }
    mavenCentral()
    gradlePluginPortal()
    maven { setUrl("https://repo.spring.io/plugins-release") }
}

private fun Project.mustBeRoot(methodName: String) {
    if (this.parent != null) {
        throw IllegalArgumentException("$methodName method only support root project.")
    }
}

fun Project.useDefault(
    jvmVersion: String = "1.8",
    includeSource: Boolean = true,
    useMavenProxy: Boolean = true,
    isBomProject: Boolean = false,
    dependencyAction: (DependencyHandlerScope.() -> Unit)? = null
) {
    if (this.parent == null) {
        buildscript {
            repositories {
                this.useDefaultRepositories()
            }
        }
    }

    if (isBomProject) {
        this.allprojects {
            apply(plugin = "java-platform")
        }
        return
    }

    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "java-library")


    this.tasks.withType(JavaCompile::class.java) {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }


//        this.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
//            kotlinOptions {
//                jvmTarget = jvmVersion
//            }
//        }


    this.configure<JavaPluginExtension> {
        withJavadocJar()
        if (includeSource) {
            withSourcesJar()
        }
    }

    this.tasks.withType(Javadoc::class.java) {
        this.isFailOnError = false
    }

    repositories {
        useDefaultRepositories(useMavenProxy)
    }

    if (this.tasks.findByName("test") != null) {
        this.tasks.withType(Test::class.java) {
            useJUnitPlatform()
        }
    }

    this.dependencies {
        this.add("implementation", platform("com.labijie.bom:lib-dependencies:${Versions.infraBomVersion}"))
        this.add("api", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}")
        this.add("api", "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}")
        /**

        testImplementation "org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version"
        testImplementation "org.junit.jupiter:junit-jupiter-api"
        testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine"
        testImplementation "org.mockito:mockito-all"
         */
        this.add("testImplementation", "org.jetbrains.kotlin:kotlin-test-junit5")
        this.add("testImplementation", "org.junit.jupiter:junit-jupiter-api")
        this.add("testImplementation", "org.junit.jupiter:junit-jupiter-engine")
        this.add("testImplementation", "org.mockito:mockito-all")

        dependencyAction?.invoke(this)
    }
}


fun Project.usePublishing(info: PomInfo, artifactName: ((p: Project) -> String)? = null) {

    this.apply(plugin = "maven-publish")
    this.apply(plugin = "signing")

    if(this.parent == null)
    {
        this.apply(plugin = "io.github.gradle-nexus.publish-plugin")
    }


    val project = this
    val artifact = artifactName?.invoke(project) ?: project.name

    this.configure<PublishingExtension> {
        publications {
            create("maven", MavenPublication::class.java) {
                artifactId = artifact
                from((components.findByName("javaPlatform") ?: components.findByName("java")))
                pom {
                    description.set(info.description)
                    url.set(info.projectUrl)
                    licenses {
                        license {
                            name.set(info.licenseName)
                            url.set(info.licenseUrl)
                        }
                    }
                    developers {
                        developer {
                            id.set(info.developerName)
                            name.set(info.developerName)
                            email.set(info.developerMail)
                        }
                    }
                    scm {
                        url.set(info.projectUrl)
                        connection.set(info.githubScmUrl)
                        developerConnection.set(info.gitUrl)
                    }
                }
            }
        }
    }

    this.configure<SigningExtension> {
        val publishing = project.the(PublishingExtension::class)

        if (project.canBeSign()) {
            this.sign(publishing.publications.findByName("maven"))
        } else {
            println("Signing information missing/incomplete for ${project.name}")
        }
    }

    if (this.extensions.findByName("nexusPublishing") != null) {
        this.extensions.configure<io.github.gradlenexus.publishplugin.NexusPublishExtension> {
            val u = project.getPropertyOrCmdArgs("PUB_USER", "u")
            val p = project.getPropertyOrCmdArgs("PUB_PWD", "p")
            val s = project.getPropertyOrCmdArgs("PUB_URL", "s") ?: "https://your-nexus-server.com/publish"
            repositories {
                sonatype {
                    if (u != null) {
                        username.set(u)
                        if (p != null) {
                            password.set(p)
                        }
                    }
                }
                create("nexus") {
                    nexusUrl.set(uri(s))
                    //snapshotRepositoryUrl.set(uri("https://your-server.com/snapshots"))
                    if(u != null) {
                        username.set(u) // defaults to project.properties["nexusUsername"]
                        if (p != null) {
                            password.set(p) // defaults to project.properties["nexusPassword"]
                        }
                    }
                }
            }
        }
    }
}
