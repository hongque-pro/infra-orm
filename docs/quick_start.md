# QuickStart

通过本向导指导你如何通过 Infra-ORM 提供的工具链更好的使用 Exposed DSL 。

## 1. 添加实体生成器配置
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

> 加入 KSP , 同时引入 Infra-ORM 的代码生成器

```kotlin
ksp {
    arg("key", "value")
}
```

这一部分是 KSP 配置，生成器支持的配置在这里加入（后面会说），默认不需要任何配置也能工作良好。

## 2. 编写 Table (Schema)类

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

## 3. 编译项目：

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

User.kt
```kotlin
public open class User {
   public var id: Long = 0L

   public var name: String = ""

   public var status: TestEnum = TestEnum.OK

   public var count: Int = 0
}

```
UserDSL.kt
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

> 生成代码包含了 User 对象作为参数的 update, insert, batchInsert， 和一些完成数据映射的帮助器方法, 但似乎还缺少一些东西,
比如 selectByPrimaryKey, deleteByPrimaryKey, updateByPrimaryKey.

## 如何获得主键方法（SelectById, UpdateById, DeleteById）


由于 KSP 是编译时完成代码结构分析，此时还未生成字节码，所以不具备反射的能力，KSP 的定位也不会提供”赋值“层级的代码分析，
所以分析不了 **UserTable** 中的代码：

```kotlin
override val primaryKey: PrimaryKey = PrimaryKey(id, name = "user_Id")
```

简单说，由于无法分析出主键是由 id 这个属性提供的，要读取主键最直接的方式就是在 id 属性上加入注解，这样可以通过 KSP 的 API 进行分析。   
但是，加入注解如果只是完成一个简单的 ID 分析似乎有点大材小用了，不妨换了一个思路，加入基类限定主键的属性名称。

这需要引入一个包（有洁癖的请放心，这个包非常干净，只依赖 exposed-core, 这个包目前只有几个基类）:

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

3. 重新编译项目，你将发现的 UserDSL 类多了三个扩展方法：

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

至此，已经有了常用的单表操作方法，同时几个用于数据映射的扩展方法也可以协助更好的使用 Exposed DSL 的强大功能。

> 生成代码样例可以在本项目的的 **dummy-project** 模块中找到

### 如何处理多主键？

这不是一个广泛需求，还会为数据库带来减益，暂不打算为了这个意义不大的需求引入一系列注解污染你的代码，你可以考虑如下方式处理这个额问题：

1. 如果可能，通过程序转换为单主键，例如 md5 hash 多个键值的方式，这样存储是单主键，但是逻辑上还是多主键。
1. 手写帮助器方法，因为生成的代码已经完成了映射类型的苦差事，多写几个万年不改的扩展方法并不难。
---
# 生成器配置

一般情况下默认配置可以良好工作，但也有一些例外，例如你的 Table 类分散在多个包，这样生成代码也会分散各处，可能你需要集中管理这些生成类,
那么以下配置可以帮到你:

> 为了降低学习成本，**Infra-ORM** 提供的配置不多，约定大于配置是不变的真理.

|参数名|说明|
|-------|-------|
| exg_package| 生成代码的包名，如果不配置，默认会在你的 Table 类的包下创建 pojo 子包，代码文件将放入其中 |
| exg_out_dir| 生成代码的目录，必须是**绝对路径**，如果不配置，默认生成到你的 Table 类所在的项目 kotlin 代码文件目录

> **exg_out_dir** 虽然要求**绝对路径**，但是你可以通过 gradle.build 中提供的变量得到项目目录,以达到相对路径的效果

:lips: 注意：**exg_out_dir** 要配置到 **XXX/src/main/kotlin** 这个层级，生成器会自动创建包目录

配置使用示例，在 gradle.build.kts 中添加如下代码:

```kotlin

ksp {
    arg("exg_package", "com.github.my.orm")
    arg("exg_out_dir", project.rootProject.childProjects["other"]!!.projectDir.absolutePath)
}

```