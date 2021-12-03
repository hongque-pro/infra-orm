#Infra-ORM

![maven central version](https://img.shields.io/maven-central/v/com.labijie.orm/exposed-core?logo=java)
![workflow status](https://img.shields.io/github/workflow/status/hongque-pro/infra-orm/Gradle%20Build%20And%20Release/main?logo=github)
![license](https://img.shields.io/github/license/hongque-pro/infra-orm?style=flat-square)

欢迎使用 **Infra-ORM**, 这是一个基于 [Exposed](https://github.com/JetBrains/Exposed)
的 ORM 框架，并且和 Spring Boot 集成良好，如果你是 Kotlin 开发者，我们推荐你试试 Exposed, 
配合我们的 Infra-ORM 可以给你带来最佳的开发体验。

## 为什么造这个轮子？

Exposed 提供了 Dao 和 DSL 编程模型，具体编程模型的争论可以看下面的 issue 连接， Infra-ORM 主要解决该讨论中的问题，
因此，你如果是 DAO 编程模型（SQL延迟发送，实体附加状态，类似 JPA 的方式）用户，请使用 Exposed 官方的库。    

讨论原帖：   
https://github.com/JetBrains/Exposed/issues/24

---

## DAO编程模型的问题：   

使用 dao 编程模型的框架很多，JAVA 领域最具代表性的要数 JPA。

1. DAO对象是一个附加”函数“功能的复杂对象，大多框架数实现都带有一个基类，你不能得到一个纯净的 POJO，这个基类可能会对序列化造成不良影响（例如 Kryo 序列化）。
2. 任何的 DAO 对象使用时都要异常小心，因为他带有状态的，漫不经心的操作很可能会造成数据库修改。
3. 学习成本高，你需要清楚的理解并发处理机制、数据库语句发送时机、缓存，上下文如何脱离和附加，极端的例子就是 JPA 和 微软的 EntityFramework（EF有关闭更改追踪的功能），
   并没有多少人能彻底掌握。
   
>> 由于上面的问题，JPA 也备受争议，理解不充分的情况下贸然使用会出现莫名其妙的 BUG，这是我们趟过的坑！！

综上，我们更倾向于选择一种轻量化，无状态的编程方式操作数据库，JAVA 环境由于语言描述能力有限，似乎只有
[Mybatis Dynamic](https://github.com/mybatis/mybatis-dynamic-sql) + Mybatis Generate 一个勉强凑合的选择（
相较于 LINQ 表达式和 C# 语言赋予 EF 的魔力，JAVA 语言这方面真是弱爆了），这也是 Mybatis 这种古老框架存活这么久的原因吧。


随着 KOTLIN 语言的出现，JAVA 领域的 ORM 有了更多风骚的操作，Exposed 更是将 kotlin 语法在 ORM 方向发挥到了极致。

不幸的是官方似乎对 DAO 模型情有独钟，并不打算在 DSL + 简单对象映射的方向上给出方案，所以有了这个轮子

## Quick Start

#### 1. 添加使用生成器 
仅对使用 kotlin dsl 的 gradle.build 支持， groovy 风格的配置文件暂未测试兼容性。   
在你的 **Table** 类所在的项目 gradle.build.kts 中加入如下内容:
```kotlin
plugins {
    id("com.google.devtools.ksp") version Versions.kspVersion
}

dependencies {
    ksp("com.labijie.orm:exposed-generator:${Versions.ormVersion}")
}

ksp {
    arg("key", "value")
}

```
>> 加入 KSP , 同时引入 Infra-ORM 的代码生成器

```kotlin
ksp {
    arg("key", "value")
}
```

这一部分是 KSP 配置，生成器支持的配置在这里加入（后面会说），默认不需要任何配置也能工作良好。

#### 2. 编写 Table (Schema)类

```kotlin
object UserTable : Table("my") {
   var id = long("id").autoIncrement()
   var name: Column<String> = varchar("name", 50)
   var status = enumeration("status", TestEnum::class)
   var count = integer("count")

   override val primaryKey: PrimaryKey = PrimaryKey(id, name = "user_Id")
}
```

此时项目目录结构如下
```text
|   build.gradle.kts
|
\---src
    +---main
    |   +---kotlin
    |   |   \---com
    |   |       \---labijie
    |   |           \---orm
    |   |               \---dummy
    |   |                       UserTable.kt
    |   |
    |   \---resources
    \---test
        +---kotlin
        \---resources

```

#### 3. 编译项目：

编译项目后，目录结构如下:

```text
|   build.gradle.kts
|
\---src
    +---main
    |   +---kotlin
    |   |   \---com
    |   |       \---labijie
    |   |           \---orm
    |   |               \---dummy
    |   |                   |   UserTable.kt
    |   |                   |
    |   |                   \---pojo
    |   |                       |   User.kt
    |   |                       |
    |   |                       \---dsl
    |   |                               UserDSL.kt
    |   |
    |   \---resources
    \---test
        +---kotlin
```

可以看到生成了 *pojo* 目录, 同时生成了 *User.kt* 文件和 *UserDSL.kt* 文件:

```kotlin
public open class User {
   public var id: Long = 0L

   public var name: String = ""

   public var status: TestEnum = TestEnum.OK

   public var count: Int = 0
}

```

```kotlin
public object UserDSL {
   public fun parseUserRow(raw: ResultRow): User {
      val plain = User()
      plain.id = raw[id]
      plain.name = raw[name]
      plain.status = raw[status]
      plain.count = raw[count]
      return plain
   }

   public fun applyUser(statement: UpdateBuilder<*>, raw: User): Unit {
      statement[id] = raw.id
      statement[name] = raw.name
      statement[status] = raw.status
      statement[count] = raw.count
   }

   public fun applyUser(statement: UpdateStatement, raw: User): Unit {
      statement[id] = raw.id
      statement[name] = raw.name
      statement[status] = raw.status
      statement[count] = raw.count
   }

   public fun ResultRow.toUser(): User = parseUserRow(this)

   public fun Iterable<ResultRow>.toUserList(): List<User> = this.map(UserDSL::parseUserRow)

   public fun UpdateBuilder<*>.apply(raw: User) = applyUser(this, raw)

   public fun UpdateStatement.apply(raw: User) = applyUser(this, raw)

   public fun UserTable.insert(raw: User): InsertStatement<Number> = UserTable.insert {
      applyUser(it, raw)
   }

   public fun UserTable.batchInsert(list: Iterable<User>): List<ResultRow> {
      val rows = UserTable.batchInsert(list) {
            entry -> applyUser(this, entry)
      }
      return rows
   }

   public fun UserTable.update(
      raw: User,
      limit: Int? = null,
      `where`: SqlExpressionBuilder.() -> Op<Boolean>
   ): Int = UserTable.update(where, limit) {
      applyUser(it, raw)
   }
}

```

- *User.kt* 是实体类, 帮助你用简单对象映射到 Exposed 的 ResultRow   
- *UserDSL* 是数据操作的扩展方法，帮助你自动完成数据映射，简化 CRUD 操作

看代码可以发现，我们有了直接将 User 对象作为参数的 update, insert, batchInsert， 和一些完成数据映射的帮助器方法, 但似乎还缺少一些东西, 
比如 selectByPrimaryKey, deleteByPrimaryKey, updateByPrimaryKey.

## 如何获得主键方法（SelectById, UpdateById, DeleteById）

由于 KSP 是编译时完成代码结构分析，此时还未生成字节码，所以不具备反射的能力，KSP 的定位也不会提供”赋值“层级的代码分析，
所以我们分析不了 **UserTable** 中的代码：
```kotlin
override val primaryKey: PrimaryKey = PrimaryKey(id, name = "user_Id")
```
简单说，我们不知道主键是由 id 这个属性提供的，要阅读主键最直接的方式就是在 id 属性上加入注解，这样可以通过 KSP 的 API 进行分析，
加入注解如果只是完成一个简单的 ID 分析我们认为过于大材小用了，因此我们换了一个思路，通过加入几个基类解决这个问题，
需要引入一个包（有洁癖的请放心，这个包非常干净，只依赖 exposed-core, 这个包目前只有几个基类）:

1. 引入包

```kotlin
dependencies {
    api("com.labijie.orm:exposed-core:${Versions.ormVersion}")
}
```

2. 改造一下 **UserTable** 的代码
```kotlin
import com.labijie.infra.orm.SimpleLongIdTable

object UserTable : SimpleLongIdTable("my", "id") {
    var name: Column<String> = varchar("name", 50)
    var status = enumeration("status", TestEnum::class)
    var count = integer("count")
}
```

让 UserTable 继承自 **SimpleLongIdTable** 即可，这样我们通过 KSP 分析代码时候只要发现这个基类，就知道你的主键是 id 属性。

3. 重新编译代码，将生成新的 DSL 类：

```kotlin
public object UserDSL {
  ....

  public fun UserTable.update(raw: User): Int = UserTable.update(raw) {
    UserTable.id eq id
  }

  public fun UserTable.deleteByPrimaryKey(id: Long): Int = UserTable.deleteWhere {
    UserTable.id eq id
  }

  public fun UserTable.selectByPrimaryKey(id: Long): User? {
    val query = UserTable.select {
      UserTable.id eq id
    }
    return query.firstOrNull()?.toUser()
  }
}

```

至此，已经有了常用的单表操作方法，同时几个用于数据映射的扩展方法也协助你更方便的使用 Exposed DSL 的强大功能。

> 生成代码样例可以在本项目的的 **dummy-project** 模块中找到

#### 如何处理多主键？

这不是一个广泛需求，还会为数据库带来减益，暂不打算为了这个意义不大的需求引入一系列注解污染你的代码，你可以考虑如下方式处理这个额问题： 

1. 如果可能，通过程序转换为单主键，例如 md5 hash 多个键值的方式，这样存储是单主键，但是逻辑上还是多主键。
1. 手写帮助器方法，因为我们已经完成了映射类型的苦差事，多写几个万年不改的扩展方法并不难。

## 生成器配置

一般情况下默认配置可以良好工作，但也有一些例外，例如你的 Table 类分散在多个包，这样生成代码也会分散各处，可能你需要集中管理这些生成类,
那么以下配置可以帮到你:

> 为了降低学习成本，**Infra-ORM** 提供的配置不多，约定大于配置是不变的真理.

|参数名|说明|
|-------|-------|
| exg_package| 生成代码的包命，如果不配置，默认会在你的 Table 类下面构建子 pojo 包，代码文件将放入其中 |
| exg_out_dir| 生成代码的目录，必须是绝对路径，如果不配置，默认生成到你的 Table 类所在的项目 kotlin 代码文件目录 

> **exg_out_dir** 要求绝对路径，但是你可以通过 gradle.build 中提供的变量得到项目目录,以达到相对路径的效果   

:lips: 注意：**exg_out_dir** 要配置到 **XXX/src/main/kotlin** 这个层级，生成器会自动创建包目录

配置使用示例，在 gradle.build.kts 中添加如下代码:

```kotlin

ksp {
    arg("exg_package", "com.github.my.orm")
    arg("exg_out_dir", project.rootProject.childProjects["other"]!!.projectDir.absolutePath)
}

```



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

