# 与 Spring Boot 项目集成

Infra-ORM 提供 exposed-starter 和 exposed-test-starter 包, 帮助你快速的与 Spring boot 项目集成并完成单元测试。

> expose-generator 并不依赖 spring， 可以在任何项目中使用，它只是根据生成你的代码为你生成 POJO 对象，不能为其他包生成代码。

## 1. 引入包

在你的 gradle.build.kts 中配置:

```kotlin
dependencies {
    api("com.labijie.orm:exposed-springboot-starter", Versions.ormVersion)
}
```

该包主要完成两个工作：
1. 使用 Spring Transaction Manager 来代理 Exposed 事务管理器
2. 自动生成数据库 Schema（可配置）

### 2. 配置自动执行数据库 DDL 来生成 Schema

- 在 application.yml 中加入配置

```yaml
infra:
  exposed:
    generate-schema:
      enabled: true
      allow-drop-columns: true
```

exposed 配置属性说明:

| 属性                      | 说明                                               |
|-------------------------|--------------------------------------------------|
| show-sql                | 是否在日志中打印 SQL 语句，默认为 **false**                    |
| generate-schema.enabled | 是否在启动时自动执行 schema ddl （自动建表、调整表结构），默认为 **false** |
| generate-schema.allow-drop-columns | 是否允许在自动执行 schema ddl 时删除多余的列，默认为 **false**       |

### 3. 通过 TableScan 注解来控制要生成的 Schema

由于自动生成数据库 Schema 是非常危险的操作（它会自动调整数据库表结构来适应 Table 类定义），不建议在生产环境自动生成 Schema.     
鉴于该操作的危险性， Infra-ORM 需要你显式的指定要自动生成 Schema 的 Table 类，指定方式是通过 TableScan 注解完成。

> #### 仅当 generate-includes 配置为 true 时需要该步骤 ！

```kotlin

@SpringBootApplication
@TableScan
@SpringBootApplication
class YourApplication

fun main(args: Array<String>) {
    runApplication<YourApplication>(*args)
}


```

**TableScan** 注册默认只扫描当前项目中的所有包，除了可以在启动类上应用它，你还可以在 AutoConfiguration 类上应用。

TableScan 提供以下属性来定义扫包逻辑：

|   属性     |       类型       |    说明    |
|-----------|------------|----------------|
| basePackages| Array&lt;String> |  包名，如果不指定 basePackages 和 basePackageClasses，表示仅扫描当前被注解的类所在的包 |
| basePackageClasses| Array&lt;KClass&lt;*>> |  通过类型来指定包，即类型所在的包 |
| excludeClasses| Array&lt;KClass&lt;*>> |  要排除的类，通过排除类来避免自动生成 Schema |



# 使用 exposed-test-starter 单元测试

Infra-ORM 同时提供了单元测试包，方便你进行 Exposed 单元测试。

## 1. 引入包

在你的 gradle.build.kts 中配置:

```kotlin
dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:<version>")
    testImplementation("org.junit.jupiter:junit-jupiter-api:<version>")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:<version>")
    testImplementation("com.labijie.orm:exposed-test-starter:<version>")
}
```

## 2. 定义你的测试上下文

在测试项目中创建 TestContext.kt :
> 测试上下文配置类你可以定义要注入到测试环境的其他 Bean。
```kotlin
@Configuration(proxyBeanMethods = false)
class TestingContext {
    
    @Bean
    fun yourBean(): YourBean {
        return YourBen()
    }
}
```

## 3. 创建你的测试类

在测试上注解 **ExposedTest** 将自动为你配置好 Exposed 所需的依赖（包括 exposed-starter）

```kotlin
import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.orm.test.ExposedTest
import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertNotNull

@ExposedTest //自动配置 Exposed 依赖
@ExtendWith(SpringExtension::class) //使用 Spring test 环境
@ContextConfiguration(classes = [TestingContext::class]) //指定测试上下文
@Transactional //开启 Exposed 事务支持
@TableScan //如果你配置了自动生成 Schema 你可以通过该注解定义需要自动创建 Schema 的 Table 类
class Tester {

    @Test
    fun testCRUD(){

        TestEntityTable.insert {
            it[name] = "ccc"
        }

        val entity = TestEntityTable.select {
            TestEntityTable.name eq "ccc"
        }.firstOrNull()

        assertNotNull(entity)
    }
}
```
