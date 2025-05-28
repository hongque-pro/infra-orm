/**
 * THIS FILE IS PART OF HuanJing (huanjing.art) PROJECT
 * Copyright (c) 2023 huanjing.art
 * @author Huanjing Team
 */
package com.labijie.orm.testing

import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.orm.test.ExposedTest
import com.labijie.orm.dummy.PostTable
import com.labijie.orm.dummy.ShopTable
import com.labijie.orm.dummy.pojo.Shop
import com.labijie.orm.dummy.pojo.dsl.ShopDSL.insert
import com.labijie.orm.dummy.pojo.dsl.ShopDSL.selectForward
import com.labijie.orm.dummy.pojo.dsl.ShopDSL.selectForwardByPrimaryKey
import com.labijie.orm.testing.context.TestingAutoConfiguration
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//使用 Spring test 环境
@ExposedTest //自动配置 Exposed 依赖 //指定测试上下文
@TableScan(basePackageClasses = [PostTable::class])
@ContextConfiguration(classes = [TestingAutoConfiguration::class])
class SelectForwardTester {

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @AfterEach
    @Rollback(false)
    fun clearData() {
        this.transactionTemplate.execute {
            ShopTable.deleteAll()
        }
    }

    @Test
    @Rollback(false)
    fun testForwardById() {

        this.transactionTemplate.execute {
            repeat(10) {
                val shop = Shop().apply {
                    this.id = it.toLong()
                    this.name = "name_${it}"
                }
                ShopTable.insert(shop)
            }
        }


        this.transactionTemplate.execute {
            var token: String? = null
            repeat(5) {
                val page = ShopTable.selectForwardByPrimaryKey(token, pageSize = 2, order = SortOrder.ASC)

                assertEquals(2, page.list.size, "page size invalid")

                assertEquals((2 * it).toLong(),page.list[0].id)
                assertEquals( (2 * it + 1).toLong(),page.list[1].id)

                token = page.forwardToken
                val hasNext = it < 4
                assertTrue(hasNext ==  !page.forwardToken.isNullOrBlank(), "Page forwarding token invalid, at $it")
            }
        }
    }

    @Test
    @Rollback(false)
    fun testForwardOrderByName() {

        val names: Array<String> = arrayOf("a", "a", "b", "b", "c", "c", "d", "d", "e", "e")

        this.transactionTemplate.execute {
            repeat(10) {
                val shop = Shop().apply {
                    this.id = it.toLong()
                    this.name = names[it]
                }


                ShopTable.insert(shop)
            }
        }


        this.transactionTemplate.execute {
            var token: String? = null
            repeat(5) {
                val page = ShopTable.selectForward(ShopTable.name, pageSize = 2, order = SortOrder.ASC, forwardToken = token)

                assertEquals(2, page.list.size, "page size invalid at $it")

                assertEquals((2 * it).toLong(),page.list[0].id)
                assertEquals( (2 * it + 1).toLong(),page.list[1].id)

                token = page.forwardToken
                val hasNext = it < 4
                assertTrue(hasNext ==  !page.forwardToken.isNullOrBlank(), "Page forwarding token invalid, at $it")
            }
        }
    }
}