package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.TestEntityTable
import com.labijie.orm.dummy.TestEntityTable.binaryCol
import com.labijie.orm.dummy.TestEntityTable.booleanCol
import com.labijie.orm.dummy.TestEntityTable.byteCol
import com.labijie.orm.dummy.TestEntityTable.charCol
import com.labijie.orm.dummy.TestEntityTable.enumCol
import com.labijie.orm.dummy.TestEntityTable.id
import com.labijie.orm.dummy.TestEntityTable.memo
import com.labijie.orm.dummy.TestEntityTable.name
import com.labijie.orm.dummy.TestEntityTable.shortCol
import com.labijie.orm.dummy.TestEntityTable.textCol
import com.labijie.orm.dummy.TestEntityTable.uidCol
import com.labijie.orm.dummy.pojo.TestEntity
import kotlin.Boolean
import kotlin.Int
import kotlin.Number
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

public object TestEntityDSL {
  public fun parseTestEntityRow(raw: ResultRow): TestEntity {
    val plain = TestEntity()
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
    plain.id = raw[id].value
    return plain
  }

  public fun applyTestEntity(statement: UpdateBuilder<*>, raw: TestEntity): Unit {
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

  public fun applyTestEntity(statement: UpdateStatement, raw: TestEntity): Unit {
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

  public fun ResultRow.toTestEntity(): TestEntity = parseTestEntityRow(this)

  public fun Iterable<ResultRow>.toTestEntityList(): List<TestEntity> =
      this.map(TestEntityDSL::parseTestEntityRow)

  public fun UpdateBuilder<*>.apply(raw: TestEntity) = applyTestEntity(this, raw)

  public fun UpdateStatement.apply(raw: TestEntity) = applyTestEntity(this, raw)

  public fun TestEntityTable.insert(raw: TestEntity): InsertStatement<Number> =
      TestEntityTable.insert {
    applyTestEntity(it, raw)
  }

  public fun TestEntityTable.insertAndGetId(raw: TestEntity): EntityID<Int> =
      TestEntityTable.insertAndGetId {
    applyTestEntity(it, raw)
  }

  public fun TestEntityTable.batchInsert(list: Iterable<TestEntity>): List<ResultRow> {
    val rows = TestEntityTable.batchInsert(list) {
      entry -> applyTestEntity(this, entry)
    }
    return rows
  }

  public fun TestEntityTable.update(
    raw: TestEntity,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>
  ): Int = TestEntityTable.update(where, limit) {
    applyTestEntity(it, raw)
  }

  public fun TestEntityTable.update(raw: TestEntity): Int = TestEntityTable.update(raw) {
    TestEntityTable.id eq id
  }

  public fun TestEntityTable.deleteByPrimaryKey(id: Int): Int = TestEntityTable.deleteWhere {
    TestEntityTable.id eq id
  }

  public fun TestEntityTable.selectByPrimaryKey(id: Int): TestEntity? {
    val query = TestEntityTable.select {
      TestEntityTable.id eq id
    }
    return query.firstOrNull()?.toTestEntity()
  }
}
