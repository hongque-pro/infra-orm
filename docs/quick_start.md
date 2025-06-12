# QuickStart

通过本向导指导你如何通过 Infra-ORM 提供的工具链更好的使用 Exposed DSL 。

## 1. 添加实体生成器配置
仅对使用 kotlin dsl 的 gradle.build 支持， groovy 风格的配置文件暂未测试兼容性。   
在你的 **Table** 类所在的项目 gradle.build.kts 中加入如下内容:
```groovy
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

如果你使用 [infra-gradle-plugin](https://github.com/hongque-pro/infra-gradle-plugin), 配置能够进一步简化

```groovy
infra {
    useKspPlugin(project("com.labijie.orm:exposed-generator:${Versions.ormVersion}"))
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


由于 KSP 是编译时完成代码结构分析，此时还未生成字节码，所以不具备反射的能力，KSP 也不不支持分析表达式（赋值语句），
所以分析不了 **UserTable** 中的代码：  

```kotlin
override val primaryKey: PrimaryKey = PrimaryKey(id, name = "user_Id")
```

> KSP 不支持表达式分析，关于这个问题可以看这里: https://github.com/google/ksp/issues/642


简单说，由于无法分析出主键是由 id 这个属性提供的，要读取主键最直接的方式就是在 id 属性上加入注解，这样可以通过 KSP 的 API 进行分析。

主键分析，有两种方式，下面分别说明。

无论何种方式我们都需要引入一个包：

```kotlin
dependencies {
    api("com.labijie.orm:exposed-core:${Versions.ormVersion}")
}
```

> 有洁癖的请放心，这个包非常干净，只依赖 exposed-core, 这个包目前只有几个基类和一些 Annotation 定义.

### 约定基类
通过使用约定好的`Table`基类来确定主键类型和列名，这种方式代码相对干净，但是只能支持单一主键，并且要求主键名固定为 `id`。  


1. 改造一下 **UserTable** 的代码
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

### 注解主键

这种方式可以非常灵活的定义主键，并且支持多主键，但是代码不是那么干净。

```kotlin
import com.labijie.infra.orm.compile.KspPrimaryKey

public open class MultiKey {

  @KspPrimaryKey
  public var key1: String = ""

  @KspPrimaryKey
  public var key2: Int = 0

}
```

生成方式和上面的文档中的方法一致。

---
# 生成器配置

一般情况下默认配置可以良好工作，但也有一些例外，例如你的 Table 类分散在多个包，这样生成代码也会分散各处，可能你需要集中管理这些生成类,
那么以下配置可以帮到你:

> 为了降低学习成本，**Infra-ORM** 提供的配置不多，约定大于配置是不变的真理.


| 参数名                   | 默认值         | 说明                                                                           |
|-----------------------|-------------|------------------------------------------------------------------------------|
| orm.pojo_package      |             | 生成代码的包名，如果不配置，默认会在你的 Table 类的包下创建 pojo 子包，代码文件将放入其中                          |
| orm.pojo_project_dir  |             | 生成代码的目录，必须是**绝对路径**，如果不配置，默认生成到你的 Table 类所在的项目根目录                            |
| orm.table_group_id    | com.example | Table 类文件所在的包 group id, 必须和 gradle.build 中配置一致，这是 GraalVM 进行 native 编译必须的    |
| orm.table_artifact_id |             | Table 类文件所在的包 artifact id, 必须和 gradle.build 中配置一致，这是 GraalVM 进行 native 编译必须的 |
| orm.orm.springboot_aot     | false       | 是否生成 springboot hint 类 (RuntimeHintsRegistrar)                                                    |    

> `orm.pojo_dir` 虽然要求**绝对路径**，但是你可以通过 gradle.build 中提供的变量得到项目目录,以达到相对路径的效果

:lips: 注意：
- `orm.pojo_project_dir` 要配置到项目**根目录**，即 `gradle.build` 文件所在目录.   
- 如果配置了`orm.pojo_project_dir`生成器会拼接子目录:    `<pojo_project_dir>/src/main/kotlin/<pojo_package>`
- 拖过没有配置 `table_artifact_id` 即使启用了 native build, 也无法生成反射配置

配置使用示例，在 gradle.build.kts 中添加如下代码:

```kotlin

ksp {
    arg("orm.out_package", "com.github.my.orm")
    arg("orm.pojo_dir", project.rootProject.childProjects["other"]!!.projectDir.absolutePath)
    arg("orm.native_build", "true")
    arg("orm.table_group_id", project.group.toString())
    arg("orm.table_artifact_id", project.name)
}

```