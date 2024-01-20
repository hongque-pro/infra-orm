@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.MultiKeyTable
import com.labijie.orm.dummy.MultiKeyTable.key1
import com.labijie.orm.dummy.MultiKeyTable.key2
import com.labijie.orm.dummy.pojo.MultiKey
import java.lang.IllegalArgumentException
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
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
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

/**
 * DSL support for MultiKeyTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.MultiKeyTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object MultiKeyDSL {
  public val MultiKeyTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    key1,
    key2,
    )
  }


  public fun parseRow(raw: ResultRow): MultiKey {
    val plain = MultiKey()
    plain.key1 = raw[key1]
    plain.key2 = raw[key2]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): MultiKey {
    val plain = MultiKey()
    if(row.hasValue(key1)) {
      plain.key1 = row[key1]
    }
    if(row.hasValue(key2)) {
      plain.key2 = row[key2]
    }
    return plain
  }

  public fun <T> MultiKeyTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    key1->String::class
    key2->Int::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'MultiKey'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> MultiKey.getColumnValue(column: Column<T>): T = when(column) {
    MultiKeyTable.key1->this.key1 as T
    MultiKeyTable.key2->this.key2 as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'MultiKey'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: MultiKey,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(key1)) && !ignore.contains(key1))
      builder[key1] = raw.key1
    if((list == null || list.contains(key2)) && !ignore.contains(key2))
      builder[key2] = raw.key2
  }

  public fun ResultRow.toMultiKey(vararg selective: Column<*>): MultiKey {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toMultiKeyList(vararg selective: Column<*>): List<MultiKey> =
      this.map {
    it.toMultiKey(*selective)
  }

  public fun MultiKeyTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: MultiKey, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: MultiKey, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun MultiKeyTable.insert(raw: MultiKey): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun MultiKeyTable.batchInsert(
    list: Iterable<MultiKey>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun MultiKeyTable.update(
    raw: MultiKey,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun MultiKeyTable.updateByPrimaryKey(raw: MultiKey, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(key1, key2)) {
    MultiKeyTable.key1.eq(raw.key1) and MultiKeyTable.key2.eq(raw.key2)
  }

  public fun MultiKeyTable.updateByPrimaryKey(
    key1: String,
    key2: Int,
    builder: MultiKeyTable.(UpdateStatement) -> Unit,
  ): Int = update({ MultiKeyTable.key1.eq(key1) and MultiKeyTable.key2.eq(key2) }, body = builder)

  public fun MultiKeyTable.deleteByPrimaryKey(key1: String, key2: Int): Int = deleteWhere {
    MultiKeyTable.key1.eq(key1) and MultiKeyTable.key2.eq(key2)
  }

  public fun MultiKeyTable.selectByPrimaryKey(
    key1: String,
    key2: Int,
    vararg selective: Column<*>,
  ): MultiKey? {
    val query = selectSlice(*selective).andWhere {
      MultiKeyTable.key1.eq(key1) and MultiKeyTable.key2.eq(key2)
    }
    return query.firstOrNull()?.toMultiKey(*selective)
  }

  public fun MultiKeyTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<MultiKey> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toMultiKeyList(*selective)
  }

  public fun MultiKeyTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      MultiKey? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toMultiKey(*selective)
  }
}
