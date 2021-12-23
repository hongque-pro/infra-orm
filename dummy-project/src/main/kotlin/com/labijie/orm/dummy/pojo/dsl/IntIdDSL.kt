package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.IntIdTable
import com.labijie.orm.dummy.IntIdTable.binaryCol
import com.labijie.orm.dummy.IntIdTable.booleanCol
import com.labijie.orm.dummy.IntIdTable.byteCol
import com.labijie.orm.dummy.IntIdTable.charCol
import com.labijie.orm.dummy.IntIdTable.enumCol
import com.labijie.orm.dummy.IntIdTable.id
import com.labijie.orm.dummy.IntIdTable.memo
import com.labijie.orm.dummy.IntIdTable.name
import com.labijie.orm.dummy.IntIdTable.shortCol
import com.labijie.orm.dummy.IntIdTable.textCol
import com.labijie.orm.dummy.IntIdTable.uidCol
import com.labijie.orm.dummy.pojo.IntId
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

/**
 * DSL support for IntIdTable
 *
 * This class made by a code generator (https://github.com/hongque-pro/infra-orm).
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.IntIdTable
 */
public object IntIdDSL {
  public fun parseIntIdRow(raw: ResultRow): IntId {
    val plain = IntId()
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

  public fun applyIntId(statement: UpdateBuilder<*>, raw: IntId): Unit {
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

  public fun applyIntId(statement: UpdateStatement, raw: IntId): Unit {
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

  public fun ResultRow.toIntId(): IntId = parseIntIdRow(this)

  public fun Iterable<ResultRow>.toIntIdList(): List<IntId> = this.map(IntIdDSL::parseIntIdRow)

  public fun UpdateBuilder<*>.apply(raw: IntId) = applyIntId(this, raw)

  public fun UpdateStatement.apply(raw: IntId) = applyIntId(this, raw)

  public fun IntIdTable.insert(raw: IntId): InsertStatement<Number> = IntIdTable.insert {
    applyIntId(it, raw)
  }

  public fun IntIdTable.insertAndGetId(raw: IntId): EntityID<Int> = IntIdTable.insertAndGetId {
    applyIntId(it, raw)
  }

  public fun IntIdTable.batchInsert(list: Iterable<IntId>): List<ResultRow> {
    val rows = IntIdTable.batchInsert(list) {
      entry -> applyIntId(this, entry)
    }
    return rows
  }

  public fun IntIdTable.update(
    raw: IntId,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>
  ): Int = IntIdTable.update(where, limit) {
    applyIntId(it, raw)
  }

  public fun IntIdTable.update(raw: IntId): Int = IntIdTable.update(raw) {
    IntIdTable.id eq id
  }

  public fun IntIdTable.deleteByPrimaryKey(id: Int): Int = IntIdTable.deleteWhere {
    IntIdTable.id eq id
  }

  public fun IntIdTable.selectByPrimaryKey(id: Int): IntId? {
    val query = IntIdTable.select {
      IntIdTable.id eq id
    }
    return query.firstOrNull()?.toIntId()
  }
}
