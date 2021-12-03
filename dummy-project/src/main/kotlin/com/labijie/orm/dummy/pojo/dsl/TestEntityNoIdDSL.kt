package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.TestEntityNoIdTable
import com.labijie.orm.dummy.TestEntityNoIdTable.binaryCol
import com.labijie.orm.dummy.TestEntityNoIdTable.booleanCol
import com.labijie.orm.dummy.TestEntityNoIdTable.byteCol
import com.labijie.orm.dummy.TestEntityNoIdTable.charCol
import com.labijie.orm.dummy.TestEntityNoIdTable.entityId
import com.labijie.orm.dummy.TestEntityNoIdTable.enumCol
import com.labijie.orm.dummy.TestEntityNoIdTable.memo
import com.labijie.orm.dummy.TestEntityNoIdTable.name
import com.labijie.orm.dummy.TestEntityNoIdTable.shortCol
import com.labijie.orm.dummy.TestEntityNoIdTable.textCol
import com.labijie.orm.dummy.TestEntityNoIdTable.uidCol
import com.labijie.orm.dummy.pojo.TestEntityNoId
import kotlin.Boolean
import kotlin.Int
import kotlin.Number
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

public object TestEntityNoIdDSL {
  public fun parseRow(raw: ResultRow): TestEntityNoId {
    val plain = TestEntityNoId()
    plain.entityId = raw[entityId]
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
    return plain
  }

  public fun applyInsert(statement: UpdateBuilder<*>, raw: TestEntityNoId): Unit {
    statement[entityId] = raw.entityId
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
  }

  public fun applyUpdate(statement: UpdateStatement, raw: TestEntityNoId): Unit {
    statement[entityId] = raw.entityId
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
  }

  public fun ResultRow.toTestEntityNoId(): TestEntityNoId = parseRow(this)

  public fun Iterable<ResultRow>.toTestEntityNoIdList(): List<TestEntityNoId> = this.map(::parseRow)

  public fun TestEntityNoIdTable.insert(raw: TestEntityNoId): InsertStatement<Number> =
      TestEntityNoIdTable.insert {
    applyInsert(it, raw)
  }

  public fun TestEntityNoIdTable.batchInsert(list: Iterable<TestEntityNoId>): List<ResultRow> {
    val rows = TestEntityNoIdTable.batchInsert(list) {
      entry -> applyInsert(this, entry)
    }
    return rows
  }

  public fun TestEntityNoIdTable.update(
    raw: TestEntityNoId,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>
  ): Int = TestEntityNoIdTable.update(where, limit) {
    applyUpdate(it, raw)
  }
}
