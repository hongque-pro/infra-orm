@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.DescribeEnumTable
import com.labijie.orm.dummy.DescribeEnumTable.key1
import com.labijie.orm.dummy.DescribeEnumTable.key2
import com.labijie.orm.dummy.DescribeEnumTable.status
import com.labijie.orm.dummy.Status
import com.labijie.orm.dummy.pojo.DescribeEnum
import java.lang.IllegalArgumentException
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.String
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.isNotEmpty
import kotlin.collections.toList
import kotlin.reflect.KClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.ReplaceStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.statements.UpsertBuilder
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert

/**
 * DSL support for DescribeEnumTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.DescribeEnumTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object DescribeEnumDSL {
  public val DescribeEnumTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    key1,
    key2,
    status,
    )
  }

  public fun parseRow(raw: ResultRow): DescribeEnum {
    val plain = DescribeEnum()
    plain.key1 = raw[key1]
    plain.key2 = raw[key2]
    plain.status = raw[status]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): DescribeEnum {
    val plain = DescribeEnum()
    if(row.hasValue(key1)) {
      plain.key1 = row[key1]
    }
    if(row.hasValue(key2)) {
      plain.key2 = row[key2]
    }
    if(row.hasValue(status)) {
      plain.status = row[status]
    }
    return plain
  }

  public fun <T> DescribeEnumTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    key1->String::class
    key2->Int::class
    status->Status::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'DescribeEnum'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> DescribeEnum.getColumnValue(column: Column<T>): T = when(column) {
    DescribeEnumTable.key1->this.key1 as T
    DescribeEnumTable.key2->this.key2 as T
    DescribeEnumTable.status->this.status as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'DescribeEnum'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: DescribeEnum,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(key1)) && !ignore.contains(key1))
      builder[key1] = raw.key1
    if((list == null || list.contains(key2)) && !ignore.contains(key2))
      builder[key2] = raw.key2
    if((list == null || list.contains(status)) && !ignore.contains(status))
      builder[status] = raw.status
  }

  public fun ResultRow.toDescribeEnum(vararg selective: Column<*>): DescribeEnum {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toDescribeEnumList(vararg selective: Column<*>): List<DescribeEnum>
      = this.map {
    it.toDescribeEnum(*selective)
  }

  public fun DescribeEnumTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: DescribeEnum, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: DescribeEnum, vararg selective: Column<*>):
      Unit = assign(this, raw, selective = selective)

  public fun DescribeEnumTable.insert(raw: DescribeEnum): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun DescribeEnumTable.upsert(
    raw: DescribeEnum,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun DescribeEnumTable.batchInsert(
    list: Iterable<DescribeEnum>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun DescribeEnumTable.update(
    raw: DescribeEnum,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun DescribeEnumTable.updateByPrimaryKey(raw: DescribeEnum, vararg selective: Column<*>):
      Int = update(raw, selective = selective, ignore = arrayOf(key1, key2)) {
    DescribeEnumTable.key1.eq(raw.key1) and DescribeEnumTable.key2.eq(raw.key2)
  }

  public fun DescribeEnumTable.updateByPrimaryKey(
    key1: String,
    key2: Int,
    builder: DescribeEnumTable.(UpdateStatement) -> Unit,
  ): Int = update({ DescribeEnumTable.key1.eq(key1) and DescribeEnumTable.key2.eq(key2) }, body =
      builder)

  public fun DescribeEnumTable.deleteByPrimaryKey(key1: String, key2: Int): Int = deleteWhere {
    DescribeEnumTable.key1.eq(key1) and DescribeEnumTable.key2.eq(key2)
  }

  public fun DescribeEnumTable.selectByPrimaryKey(
    key1: String,
    key2: Int,
    vararg selective: Column<*>,
  ): DescribeEnum? {
    val query = selectSlice(*selective).andWhere {
      DescribeEnumTable.key1.eq(key1) and DescribeEnumTable.key2.eq(key2)
    }
    return query.firstOrNull()?.toDescribeEnum(*selective)
  }

  public fun DescribeEnumTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Query):
      List<DescribeEnum> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toDescribeEnumList(*selective)
  }

  public fun DescribeEnumTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Query):
      DescribeEnum? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toDescribeEnum(*selective)
  }

  public fun DescribeEnumTable.replace(raw: DescribeEnum): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
