/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.infra.orm.testing

import com.labijie.infra.orm.ExposedUtils.checkExcessiveColumns
import com.labijie.infra.orm.SimpleLongIdTable
import com.labijie.infra.orm.annotation.TableScan
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test


@Suppress("SqlDialectInspection", "SqlNoDataSourceInspection", "SqlSourceToSinkFlow")
@EnableAutoConfiguration
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestingContext::class])
@TableScan
class SchemaChangesProcessorTester {

    object BeforeTable : SimpleLongIdTable("t1") {
        val name1 = varchar("name1", 32).index()
        val name2 = varchar("name2", 32).index()
        val delete = bool("deleted").index()
    }

    object AfterTable : SimpleLongIdTable("t1") {
        val name2 = varchar("name2", 32).index()
    }

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var jdbcClient: JdbcClient

    companion object {
        fun JdbcClient.getColumns(table: String): MutableList<String> {
            val sql = "SHOW COLUMNS FROM ${table};"
            return this.sql(sql).query { rs, _ ->
                rs.getString("FIELD").lowercase()
            }.list()
        }
    }

    @Test
    fun testColumnDrop() {
        this.transactionTemplate.execute {
            with(TransactionManager.current()) {

                val sql = SchemaUtils.statementsRequiredToActualizeScheme(BeforeTable)
                this.queryTimeout = 30
                this.execInBatch(sql)
                commit()
                currentDialect.resetCaches()
            }
        }

        val columns = jdbcClient.getColumns(BeforeTable.tableName).toTypedArray()
        Assertions.assertArrayEquals(arrayOf(
            BeforeTable.id.name,
            BeforeTable.name1.name,
            BeforeTable.name2.name,
            BeforeTable.delete.name), columns)


        this.transactionTemplate.execute {
            with(TransactionManager.current()) {
                val sql = checkExcessiveColumns(AfterTable)
                this.queryTimeout = 30
                this.execInBatch(sql.map { it.sql })
                commit()
                currentDialect.resetCaches()
                println(sql.joinToString(System.lineSeparator()) { it.sql })
            }
        }

        val columnsAfter = jdbcClient.getColumns(BeforeTable.tableName).toTypedArray()
        Assertions.assertArrayEquals(arrayOf(BeforeTable.id.name, BeforeTable.name2.name), columnsAfter)

    }

}