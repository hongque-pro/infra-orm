/**
 * @author Anders Xiao
 * @date 2024-10-01
 */
package com.labijie.infra.orm.testing

import com.labijie.infra.orm.test.ExposedTest
import com.labijie.infra.orm.testing.tables.TestTable
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test


@ExposedTest
@EnableAutoConfiguration
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestingContext::class])
class JdbcTester {


    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate
    @Test
    fun exceptionTranslateTest() {

        Assertions.assertThrowsExactly(DuplicateKeyException::class.java) {
            this.transactionTemplate.execute {
                TestTable.insert {
                    it[id] = 1
                    it[name1] = "name1"
                    it[name2] = "name2"
                    it[delete] = false
                }

                TestTable.insert {
                    it[id] = 1
                    it[name1] = "name1"
                    it[name2] = "name2"
                    it[delete] = false
                }
            }
        }
    }
}