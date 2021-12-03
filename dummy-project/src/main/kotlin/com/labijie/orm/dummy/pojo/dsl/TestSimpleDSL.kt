package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.TestSimpleTable
import com.labijie.orm.dummy.TestSimpleTable.binaryCol
import com.labijie.orm.dummy.TestSimpleTable.booleanCol
import com.labijie.orm.dummy.TestSimpleTable.byteCol
import com.labijie.orm.dummy.TestSimpleTable.charCol
import com.labijie.orm.dummy.TestSimpleTable.enumCol
import com.labijie.orm.dummy.TestSimpleTable.id
import com.labijie.orm.dummy.TestSimpleTable.memo
import com.labijie.orm.dummy.TestSimpleTable.name
import com.labijie.orm.dummy.TestSimpleTable.shortCol
import com.labijie.orm.dummy.TestSimpleTable.textCol
import com.labijie.orm.dummy.TestSimpleTable.uidCol
import com.labijie.orm.dummy.pojo.TestSimple
import kotlin.Boolean
import kotlin.Int
import kotlin.Number
import kotlin.String
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

public object TestSimpleDSL {
  public fun parseTestSimpleRow(raw: ResultRow): TestSimple {
    val plain = TestSimple()
    plain.name = raw[name]
    plain.memo = raw[memo]
    plain.charCol = raw[charCol]
    plain.textCol = raw[textCol]
    plain.enumCol = raw[enumCol]
    plain.binaryCol = raw[binaryCol]
    plain.uidCol = raw[uidCol]
    plain.shortCol = raw[shortCol]
    plain.booleanCol = raw[booleanCol]
    plain.byteCol = raw[byteCol]
    plain.id = raw[id]
    return plain
  }

  public fun applyTestSimple(statement: UpdateBuilder<*>, raw: TestSimple): Unit {
    statement[name] = raw.name
    statement[memo] = raw.memo
    statement[charCol] = raw.charCol
    statement[textCol] = raw.textCol
    statement[enumCol] = raw.enumCol
    statement[binaryCol] = raw.binaryCol
    statement[uidCol] = raw.uidCol
    statement[shortCol] = raw.shortCol
    statement[booleanCol] = raw.booleanCol
    statement[byteCol] = raw.byteCol
    statement[id] = raw.id
  }

  public fun applyTestSimple(statement: UpdateStatement, raw: TestSimple): Unit {
    statement[name] = raw.name
    statement[memo] = raw.memo
    statement[charCol] = raw.charCol
    statement[textCol] = raw.textCol
    statement[enumCol] = raw.enumCol
    statement[binaryCol] = raw.binaryCol
    statement[uidCol] = raw.uidCol
    statement[shortCol] = raw.shortCol
    statement[booleanCol] = raw.booleanCol
    statement[byteCol] = raw.byteCol
    statement[id] = raw.id
  }

  public fun ResultRow.toTestSimple(): TestSimple = parseTestSimpleRow(this)

  public fun Iterable<ResultRow>.toTestSimpleList(): List<TestSimple> =
      this.map(TestSimpleDSL::parseTestSimpleRow)

  public fun UpdateBuilder<*>.apply(raw: TestSimple) = applyTestSimple(this, raw)

  public fun UpdateStatement.apply(raw: TestSimple) = applyTestSimple(this, raw)

  public fun TestSimpleTable.insert(raw: TestSimple): InsertStatement<Number> =
      TestSimpleTable.insert {
    applyTestSimple(it, raw)
  }

  public fun TestSimpleTable.batchInsert(list: Iterable<TestSimple>): List<ResultRow> {
    val rows = TestSimpleTable.batchInsert(list) {
      entry -> applyTestSimple(this, entry)
    }
    return rows
  }

  public fun TestSimpleTable.update(
    raw: TestSimple,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>
  ): Int = TestSimpleTable.update(where, limit) {
    applyTestSimple(it, raw)
  }

  public fun TestSimpleTable.update(raw: TestSimple): Int = TestSimpleTable.update(raw) {
    TestSimpleTable.id eq id
  }

  public fun TestSimpleTable.deleteByPrimaryKey(id: String): Int = TestSimpleTable.deleteWhere {
    TestSimpleTable.id eq id
  }

  public fun TestSimpleTable.selectByPrimaryKey(id: String): TestSimple? {
    val query = TestSimpleTable.select {
      TestSimpleTable.id eq id
    }
    return query.firstOrNull()?.toTestSimple()
  }
}
