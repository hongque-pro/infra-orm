<div align="center">
<h1>Infra-ORM</h1>
</div>
<br>

<div align="center">

![maven central version](https://img.shields.io/maven-central/v/com.labijie.orm/exposed-core?logo=java)
![workflow status](https://img.shields.io/github/actions/workflow/status/hongque-pro/infra-orm/build.yml)
![license](https://img.shields.io/github/license/hongque-pro/infra-orm?style=flat-square)

</div>

<div align="center">
<strong>Document</strong>: 
<a href="docs/quick_start.md">Quick Start</a>
| <a href="docs/use_spring.md">Work with SpringBoot</a>
</div>

<br>
<br>

欢迎使用 **Infra-ORM**, 这是一个基于 [Exposed](https://github.com/JetBrains/Exposed)
的 ORM 框架，可以和 Spring Boot 集成良好，如果你是 Kotlin 开发者，推荐你试试 Exposed, 
配合 Infra-ORM 可以给你带来最佳的开发体验。

## What news in 2.1.x

- Spring Boot AOT support, got a consistent experience in GraalVM native enviroment.
- JDK **21** is used by default .
- Upgrade to Spring Boot **3.5** .

> We are currently in the process of migrating to GraalVM in our project.    
> GraalVM supported still experimental.   
> We will continuously validate the GraalVM compatibility of Infra-ORM within our project.   

## 基于表结构的 Code First 开发模式

### 1. 引入插件
示例中使用 `com.labijie.infra` gradle 插件简化配置

```ktolin

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

Ksp 参数

| 参数名                   | 默认值   | 说明                                                                        |
|-----------------------|-------|---------------------------------------------------------------------------| 
| orm.pojo_package      |       | 生成代码的包名，如果不配置，默认会在你的 Table 类的包下创建 pojo 子包，代码文件将放入其中                       |
| orm.pojo_project_dir  |       | 生成代码的目录，必须是**绝对路径**，如果不配置，默认生成到你的 Table 类所在的项目根目录                         |
| orm.springboot_aot    | false | **GraalVM** 支持： 启用 spring  AOT 集成，注册实体反射 RuntimeHint 和表的 TableScale 的 native 支持。 |    


### 2. 编写表结构类

```kotlin

object PostTable : SimpleLongIdTable("posts", "id") {
    val title: Column<String> = varchar("name", 50)
    val status = enumeration("status", TestEnum::class)
    val description = varchar("desc", 255)
}

```

或

```kotlin

//通过注解可以自定义主键属性名称（postId 代替 SimpleLongIdTable 的 id）

object PostTable: Table("posts") {

  @KspPrimaryKey
  val postId = long("post_id") 
    
  override val primaryKey: PrimaryKey
        get() = PrimaryKey(postId)
}

```

多主键表支持

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

> **注意**: Table 是一个 `object` , 请勿使用 `class` .

### 3. 生成 POJO 和 DSL 代码
执行 gradle 命令
```shell
gradle kspKotlin
```

该命令将自动为你生成 `Post` 类 (POJO) 和 `PostDSL` (Table 扩展方法类)。

```kotlin

public class Post {
  public var title: String = ""

  public var status: TestEnum = TestEnum.OK

  public var description: String = ""

  public var id: Long = 0L
}

```

4. 在代码中使用 DSL 生成代码

**Select**
```kotlin
//select by primary key
val selectedPost: Post? = PostTable.selectByPrimaryKey(123)

//select one by a column
val postItem: Post? = PostTable.selectOne {
    andWhere { PostTable.title eq  "Test" }
}

//select multiple by a column
val postList: List<Post> = PostTable.selectMany {
    andWhere { PostTable.title eq  "Test" }
}

//select multiple by a column, only select title, description columns
val postListSelective: List<Post> = PostTable.selectMany(PostTable.title, PostTable.description) {
    andWhere { PostTable.title like  "T%" }
}
```

**Insert**
```kotlin

val post = Post().apply {
    this.id = 123
    this.title = "Test"
    this.description = "Just a test."
}

//insert
PostTable.insert(post)

```

**Update**

```kotlin
//update all columns by primary key
PostTable.updateByPrimaryKey(post)

//update by primary key, and only update title column.
PostTable.updateByPrimaryKey(post, PostTable.title)

//update by a column, and only update description column.
PostTable.update(post, selective =  arrayOf(PostTable.description), limit = 1) {
    PostTable.title.eq("Test")
}


//Update or insert
PostTable.upsert(post)

//Replace
PostTable.replace(post)
```

**Delete** 
```kotlin
//delete by primary key
PostTable.deleteByPrimaryKey(123)

//delete many
PostTable.deleteWhere {
    PostTable.title inList listOf("Test", "Test1")
}

```

更多 DSL 方法，请参考 [Exposed](https://github.com/JetBrains/Exposed) 文档：

[https://jetbrains.github.io/Exposed/deep-dive-into-dsl.html](https://jetbrains.github.io/Exposed/deep-dive-into-dsl.html)


**Infra-Orm** 详细使用方法请阅读文档：
- [Quick Start](docs/quick_start.md)
- [Work with SpringBoot](docs/use_spring.md)

---



## 为什么造这个轮子？

Exposed 提供了 Dao 和 DSL 编程模型，具体编程模型的争论可以看下面的 issue 连接， Infra-ORM 主要解决该讨论中的问题，
因此，你如果是 DAO 编程模型（SQL延迟发送，实体附加状态，这是另一个 JPA ？）爱好者，请无视本项目，使用 Exposed 官方的 DAO 包。    

DAO VS DSL 讨论原帖：   
https://github.com/JetBrains/Exposed/issues/24

---

## DAO编程模型的问题：   

使用 dao 编程模型的框架很多，JAVA 领域最具代表性的要数 JPA。

1. DAO对象是一个附加”函数“功能的复杂对象，大多框架数实现都带有一个基类，你不能得到一个纯净的 POJO，这个基类可能会对序列化造成不良影响（例如 Kryo 序列化）。
2. 任何的 DAO 对象使用时都要异常小心，因为他带有状态的，漫不经心的操作很可能会造成数据库修改。
3. 学习成本高，你需要清楚的理解并发处理机制、数据库语句发送时机、缓存，上下文如何脱离和附加，极端的例子就是 JPA 和 微软的 EntityFramework（EF有关闭更改追踪
和 CodeFirst 模式避免这些问题），复杂的 DAO 机制会让很多人无法彻底掌握这些框架。

> 由于上面的问题，JPA 也备受争议，理解不充分的情况下贸然使用会出现莫名其妙的 BUG，这是我们趟过的坑！！

综上，我们需要一种轻量化、无状态、强类型的编程方式操作数据库，JAVA 环境由于语言描述能力有限，似乎只有
[Mybatis Dynamic](https://github.com/mybatis/mybatis-dynamic-sql) + Mybatis Generate 一个勉强及格的答案，这也是 Mybatis 这种古老框架存活这么久的原因吧。

贴一段看看 C# 方向的 EntityFramework 的语法，你就知道 JAVA 领域这些框架有多弱：

```csharp
var query = from photo in context.Set<PersonPhoto>()
            join person in context.Set<Person>()
                on new { Id = (int?)photo.PersonPhotoId, photo.Caption }
                equals new { Id = person.PhotoId, Caption = "SN" }
            select new { person, photo };
```


随着 KOTLIN 语言的出现，JAVA 领域的 ORM 有了更多风骚的操作，Exposed 更是将 kotlin 语法在 ORM 方向发挥到了极致，附上 Exposed 文档，自己感受一下：

https://github.com/JetBrains/Exposed/wiki/DSL

不幸的是官方似乎对 DAO 模型情有独钟，并不打算在 DSL + 简单对象映射的方向上给出方案，所以有了这个轮子。


## 数据库支持

得益于 Exposed 良好的跨数据数据库特性，Infra-ORM 支持以下数据库：

* H2
* MySQL
* MariaDB
* Oracle
* PostgreSQL
* PostgreSQL
* SQL Server
* SQLite

