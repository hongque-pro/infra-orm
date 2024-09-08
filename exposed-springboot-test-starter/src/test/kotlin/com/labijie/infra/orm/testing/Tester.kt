package com.labijie.infra.orm.testing

import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.orm.configuration.InfraExposedAutoConfiguration
import com.labijie.infra.orm.test.ExposedTest
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExposedTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestingContext::class])
@TableScan
open class Tester {

    @Autowired
    private lateinit var tables: ObjectProvider<Table>

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Test
    @Transactional
    open fun testTableAutoLoaded(){
        assertEquals(1, tables.count())
    }

    @Test
    @Transactional
    open fun testCRUD(){

        TestEntityTable.insert {
            it[id] = 123
            it[name] = "ccc"
        }

        val entity = TestEntityTable.selectAll().where {
            TestEntityTable.name eq "ccc"
        }.firstOrNull()

        assertNotNull(entity)
    }

    @Test
    @Transactional
    open fun testException(){

        TestEntityTable.insert {
            it[id] = 123
            it[name] = "ccc"
        }

        TestEntityTable.insert {
            it[id] = 123
            it[name] = "ccc"
        }
    }

}