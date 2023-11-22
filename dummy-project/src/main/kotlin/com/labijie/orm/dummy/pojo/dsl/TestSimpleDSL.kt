@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
import com.labijie.orm.dummy.TestEnum
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
import java.lang.IllegalArgumentException
import java.util.Base64
import java.util.UUID
import kotlin.Array
import kotlin.Boolean
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Char
import kotlin.Comparable
import kotlin.Int
import kotlin.Number
import kotlin.Short
import kotlin.String
import kotlin.Unit
import kotlin.collections.Collection
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.isNotEmpty
import kotlin.collections.last
import kotlin.collections.toList
import kotlin.reflect.KClass
import kotlin.text.Charsets
import kotlin.text.toByteArray
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

/**
 * DSL support for TestSimpleTable
 *
 * This class made by a code generator (https://github.com/hongque-pro/infra-orm).
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.TestSimpleTable
 */
public object TestSimpleDSL {
  public val TestSimpleTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    name,
    memo,
    charCol,
    textCol,
    enumCol,
    binaryCol,
    uidCol,
    shortCol,
    booleanCol,
    byteCol,
    id,
    )
  }


  public fun parseRow(raw: ResultRow): TestSimple {
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

  public fun parseRowSelective(row: ResultRow): TestSimple {
    val plain = TestSimple()
    if(row.hasValue(name)) {
      plain.name = row[name]
    }
    if(row.hasValue(memo)) {
      plain.memo = row[memo]
    }
    if(row.hasValue(charCol)) {
      plain.charCol = row[charCol]
    }
    if(row.hasValue(textCol)) {
      plain.textCol = row[textCol]
    }
    if(row.hasValue(enumCol)) {
      plain.enumCol = row[enumCol]
    }
    if(row.hasValue(binaryCol)) {
      plain.binaryCol = row[binaryCol]
    }
    if(row.hasValue(uidCol)) {
      plain.uidCol = row[uidCol]
    }
    if(row.hasValue(shortCol)) {
      plain.shortCol = row[shortCol]
    }
    if(row.hasValue(booleanCol)) {
      plain.booleanCol = row[booleanCol]
    }
    if(row.hasValue(byteCol)) {
      plain.byteCol = row[byteCol]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> TestSimpleTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    name->String::class
    memo->String::class
    charCol->Char::class
    textCol->String::class
    enumCol->TestEnum::class
    binaryCol->ByteArray::class
    uidCol->UUID::class
    shortCol->Short::class
    booleanCol->Boolean::class
    byteCol->Byte::class
    id->String::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'TestSimple'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> TestSimple.getColumnValue(column: Column<T>): T = when(column) {
    TestSimpleTable.name->this.name as T
    TestSimpleTable.memo->this.memo as T
    TestSimpleTable.charCol->this.charCol as T
    TestSimpleTable.textCol->this.textCol as T
    TestSimpleTable.enumCol->this.enumCol as T
    TestSimpleTable.binaryCol->this.binaryCol as T
    TestSimpleTable.uidCol->this.uidCol as T
    TestSimpleTable.shortCol->this.shortCol as T
    TestSimpleTable.booleanCol->this.booleanCol as T
    TestSimpleTable.byteCol->this.byteCol as T
    TestSimpleTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'TestSimple'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: TestSimple,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    if((selective == null || selective.contains(name)) && !ignore.contains(name))
      builder[name] = raw.name
    if((selective == null || selective.contains(memo)) && !ignore.contains(memo))
      builder[memo] = raw.memo
    if((selective == null || selective.contains(charCol)) && !ignore.contains(charCol))
      builder[charCol] = raw.charCol
    if((selective == null || selective.contains(textCol)) && !ignore.contains(textCol))
      builder[textCol] = raw.textCol
    if((selective == null || selective.contains(enumCol)) && !ignore.contains(enumCol))
      builder[enumCol] = raw.enumCol
    if((selective == null || selective.contains(binaryCol)) && !ignore.contains(binaryCol))
      builder[binaryCol] = raw.binaryCol
    if((selective == null || selective.contains(uidCol)) && !ignore.contains(uidCol))
      builder[uidCol] = raw.uidCol
    if((selective == null || selective.contains(shortCol)) && !ignore.contains(shortCol))
      builder[shortCol] = raw.shortCol
    if((selective == null || selective.contains(booleanCol)) && !ignore.contains(booleanCol))
      builder[booleanCol] = raw.booleanCol
    if((selective == null || selective.contains(byteCol)) && !ignore.contains(byteCol))
      builder[byteCol] = raw.byteCol
    if((selective == null || selective.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toTestSimple(vararg selective: Column<*>): TestSimple {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toTestSimpleList(vararg selective: Column<*>): List<TestSimple> =
      this.map {
    it.toTestSimple(*selective)
  }

  public fun TestSimpleTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      TestSimpleTable.slice(selective.toList()).selectAll()
    }
    else {
      TestSimpleTable.selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: TestSimple, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: TestSimple, vararg selective: Column<*>): Unit
      = assign(this, raw, selective = selective)

  public fun TestSimpleTable.insert(raw: TestSimple): InsertStatement<Number> =
      TestSimpleTable.insert {
    assign(it, raw)
  }

  public fun TestSimpleTable.batchInsert(
    list: Iterable<TestSimple>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = TestSimpleTable.batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun TestSimpleTable.update(
    raw: TestSimple,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = TestSimpleTable.update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun TestSimpleTable.updateByPrimaryKey(raw: TestSimple, vararg selective: Column<*>): Int =
      TestSimpleTable.update(raw, selective = selective, ignore = arrayOf(id)) {
    TestSimpleTable.id eq id
  }

  public fun TestSimpleTable.deleteByPrimaryKey(id: String): Int = TestSimpleTable.deleteWhere {
    TestSimpleTable.id eq id
  }

  public fun TestSimpleTable.selectByPrimaryKey(id: String, vararg selective: Column<*>):
      TestSimple? {
    val query = TestSimpleTable.selectSlice(*selective).andWhere {
      TestSimpleTable.id eq id
    }
    return query.firstOrNull()?.toTestSimple(*selective)
  }

  public fun TestSimpleTable.selectByPrimaryKeys(ids: Iterable<String>, vararg
      selective: Column<*>): List<TestSimple> {
    val query = TestSimpleTable.selectSlice(*selective).andWhere {
      TestSimpleTable.id inList ids
    }
    return query.toTestSimpleList(*selective)
  }

  public fun TestSimpleTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<TestSimple> {
    val query = TestSimpleTable.selectSlice(*selective)
    `where`.invoke(query)
    return query.toTestSimpleList(*selective)
  }

  public fun TestSimpleTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      TestSimple? {
    val query = TestSimpleTable.selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toTestSimple(*selective)
  }

  public fun TestSimpleTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<TestSimple> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = TestSimpleTable.selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less it }
        else-> query.andWhere { id greater it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(id, order)
    val list = sorted.limit(pageSize).toTestSimpleList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> TestSimpleTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<TestSimple> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val kp = forwardToken?.let { decodeToken(it) }
    val offsetKey = kp?.first
    val excludeKeys = kp?.second
    val query = TestSimpleTable.selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { sortColumn lessEq it }
        else-> query.andWhere { sortColumn greaterEq it }
      }
    }
    excludeKeys?.let {
      if(it.isNotEmpty()) {
        query.andWhere { id notInList it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(Pair(sortColumn, order), Pair(id, order))
    val list = sorted.limit(pageSize).toTestSimpleList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        TestSimple::id)
    return OffsetList(list, token)
  }
}
