package com.labijie.infra.orm.testing

import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.orm.test.ExposedTest
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExposedTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestingContext::class])
@TableScan
class Tester {

    @Autowired
    private lateinit var tables: ObjectProvider<Table>

    @Test
    @Transactional
    fun testTableAutoLoaded(){
        assertEquals(1, tables.count())
    }

    @Test
    @Transactional
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