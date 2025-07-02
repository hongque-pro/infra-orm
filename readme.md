<div align="center">
<h1>Infra-ORM</h1>
</div>
<br>

[简体中文](./readme_zh.md)

<br>

<div align="center">

![maven central version](https://img.shields.io/maven-central/v/com.labijie.orm/exposed-core?logo=java)
![workflow status](https://img.shields.io/github/actions/workflow/status/hongque-pro/infra-orm/build.yml)
![license](https://img.shields.io/github/license/hongque-pro/infra-orm?style=flat-square)
![Static Badge](https://img.shields.io/badge/GraalVM-supported-green?style=flat&logoColor=blue&labelColor=orange)

</div>

<div align="center">
<strong>Document</strong>: 
<a href="docs/quick_start.md">Quick Start</a>
| <a href="docs/use_spring.md">Work with SpringBoot</a>
</div>

<br>
<br>

Welcome to **Infra-ORM**, an ORM framework based on [Exposed](https://github.com/JetBrains/Exposed),
designed to integrate well with Spring Boot.  
If you're a Kotlin developer, we recommend trying Exposed —  
combined with Infra-ORM, it brings you the best development experience.

## What’s New in 2.1.x

- Spring Boot AOT support, providing a consistent experience in GraalVM native environment.
- JDK **21** is used by default.
- Upgraded to Spring Boot **3.5**.

> We are currently in the process of migrating to GraalVM in our project.  
> GraalVM support is still experimental.  
> We will continuously validate the GraalVM compatibility of Infra-ORM within our project.

## Code-First Development Based on Table Structure

### 1. Apply the Plugin

This example uses the `com.labijie.infra` Gradle plugin to simplify configuration:

```kotlin
plugins {
    id("com.google.devtools.ksp") version <ksp plugin version>
}

dependencies {
    ksp("com.labijie.orm:exposed-generator:<infra exposed version>")
    implementation(project("com.labijie.infra:exposed-starter"))
}

ksp {
    arg("orm.springboot_aot ", "true")
    ...
}
```

KSP Parameters

| Name                           | Default | Description                                                                                                                                           |
|--------------------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| orm.pojo_package               |         | Package name for generated code. If not configured, a `pojo` subpackage will be created under the Table class package.                                |
| orm.pojo_project_dir           |         | Directory to generate code to, must be an **absolute path**. If not set, it defaults to the root directory of the project containing the Table class. |
| orm.pojo_kotlin_serializable   | false   | Whether to add **@Serializable** to the POJO class to support [kotlin-serialization](https://github.com/Kotlin/kotlinx.serialization)                 |
| orm.springboot_aot             | false   | **GraalVM** support: enable Spring AOT integration, register RuntimeHints for entity classes and native support for TableScale.                       |


### 2. Define Table Classes

```kotlin
object PostTable : SimpleLongIdTable("posts", "id") {
    val title: Column<String> = varchar("name", 50)
    val status = enumeration("status", TestEnum::class)
    val description = varchar("desc", 255)
}
```

```kotlin
object PostTable: Table("posts") {

  @KspPrimaryKey
  val postId = long("post_id") 

  override val primaryKey: PrimaryKey
        get() = PrimaryKey(postId)
}
```

```kotlin
import com.labijie.infra.orm.compile.KspPrimaryKey
import org.jetbrains.exposed.sql.Table

object MultiKeyTable: Table("multi_key_table") {

  @KspPrimaryKey
  val key1 = varchar("key1", 32)

  @KspPrimaryKey
  val key2 = varchar("key2", 32)

  override val primaryKey: PrimaryKey
        get() = PrimaryKey(key1, key2)
}
```

> **Note**: The table must be declared as an `object`, not a `class`.

### 3. Generate POJO and DSL Code

```shell
gradle kspKotlin
```

```kotlin
public class Post {
  public var title: String = ""

  public var status: TestEnum = TestEnum.OK

  public var description: String = ""

  public var id: Long = 0L
}
```

### 4. Use DSL in Your Code

```kotlin
val selectedPost: Post? = PostTable.selectByPrimaryKey(123)

val postItem: Post? = PostTable.selectOne {
    andWhere { PostTable.title eq  "Test" }
}

val postList: List<Post> = PostTable.selectMany {
    andWhere { PostTable.title eq  "Test" }
}

val postListSelective: List<Post> = PostTable.selectMany(PostTable.title, PostTable.description) {
    andWhere { PostTable.title like  "T%" }
}
```

```kotlin
val post = Post().apply {
    this.id = 123
    this.title = "Test"
    this.description = "Just a test."
}

PostTable.insert(post)
```

```kotlin
PostTable.updateByPrimaryKey(post)

PostTable.updateByPrimaryKey(post, PostTable.title)

PostTable.update(post, selective =  arrayOf(PostTable.description), limit = 1) {
    PostTable.title.eq("Test")
}

PostTable.upsert(post)

PostTable.replace(post)
```

```kotlin
PostTable.deleteByPrimaryKey(123)

PostTable.deleteWhere {
    PostTable.title inList listOf("Test", "Test1")
}
```

[https://jetbrains.github.io/Exposed/deep-dive-into-dsl.html](https://jetbrains.github.io/Exposed/deep-dive-into-dsl.html)

- [Quick Start](docs/quick_start.md)
- [Work with SpringBoot](docs/use_spring.md)

---

## Supported Databases

* H2
* MySQL
* MariaDB
* Oracle
* PostgreSQL
* SQL Server
* SQLite