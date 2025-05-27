@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
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
import kotlin.Long
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
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
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
 * DSL support for TestSimpleTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.TestSimpleTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
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

  private fun <T> TestSimple.getColumnValueString(column: Column<T>): String = when(column) {
    TestSimpleTable.name->this.name
    TestSimpleTable.memo->this.memo.orEmpty()
    TestSimpleTable.charCol->this.charCol.toString()

    TestSimpleTable.textCol->this.textCol
    TestSimpleTable.binaryCol->com.labijie.infra.orm.ExposedConverter.byteArrayToString(this.binaryCol)

    TestSimpleTable.uidCol->this.uidCol.toString()

    TestSimpleTable.shortCol->this.shortCol.toString()

    TestSimpleTable.booleanCol->this.booleanCol.toString()

    TestSimpleTable.byteCol->this.byteCol.toString()

    TestSimpleTable.id->this.id
    else->throw
        IllegalArgumentException("""Can ot converter value of TestSimple::${column.name} to string.""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  private fun <T> parseColumnValue(valueString: String, column: Column<T>): T {
    val value = when(column) {
      TestSimpleTable.name -> valueString
      TestSimpleTable.memo -> valueString
      TestSimpleTable.charCol ->valueString.first()
      TestSimpleTable.textCol -> valueString
      TestSimpleTable.binaryCol ->com.labijie.infra.orm.ExposedConverter.stringToByteArray(valueString)
      TestSimpleTable.uidCol ->com.labijie.infra.orm.ExposedConverter.stringToUUID(valueString)
      TestSimpleTable.shortCol ->valueString.toShort()
      TestSimpleTable.booleanCol ->valueString.toBoolean()
      TestSimpleTable.byteCol ->com.labijie.infra.orm.ExposedConverter.stringToByteArray(valueString)
      TestSimpleTable.id -> valueString
      else->throw
          IllegalArgumentException("""Can ot converter value of TestSimple::${column.name} to string.""")
    }
    return value as T
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
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(name)) && !ignore.contains(name))
      builder[name] = raw.name
    if((list == null || list.contains(memo)) && !ignore.contains(memo))
      builder[memo] = raw.memo
    if((list == null || list.contains(charCol)) && !ignore.contains(charCol))
      builder[charCol] = raw.charCol
    if((list == null || list.contains(textCol)) && !ignore.contains(textCol))
      builder[textCol] = raw.textCol
    if((list == null || list.contains(enumCol)) && !ignore.contains(enumCol))
      builder[enumCol] = raw.enumCol
    if((list == null || list.contains(binaryCol)) && !ignore.contains(binaryCol))
      builder[binaryCol] = raw.binaryCol
    if((list == null || list.contains(uidCol)) && !ignore.contains(uidCol))
      builder[uidCol] = raw.uidCol
    if((list == null || list.contains(shortCol)) && !ignore.contains(shortCol))
      builder[shortCol] = raw.shortCol
    if((list == null || list.contains(booleanCol)) && !ignore.contains(booleanCol))
      builder[booleanCol] = raw.booleanCol
    if((list == null || list.contains(byteCol)) && !ignore.contains(byteCol))
      builder[byteCol] = raw.byteCol
    if((list == null || list.contains(id)) && !ignore.contains(id))
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
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: TestSimple, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: TestSimple, vararg selective: Column<*>): Unit
      = assign(this, raw, selective = selective)

  public fun TestSimpleTable.insert(raw: TestSimple): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun TestSimpleTable.insertIgnore(raw: TestSimple): InsertStatement<Long> = insertIgnore {
    assign(it, raw)
  }

  public fun TestSimpleTable.upsert(
    raw: TestSimple,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun TestSimpleTable.batchInsert(
    list: Iterable<TestSimple>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun TestSimpleTable.batchUpsert(
    list: Iterable<TestSimple>,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    shouldReturnGeneratedValues: Boolean = false,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): List<ResultRow> {
    val rows =  batchUpsert(data = list, keys = arrayOf(id), onUpdate = onUpdate, onUpdateExclude =
        onUpdateExclude, where = where, shouldReturnGeneratedValues = shouldReturnGeneratedValues) {
      data: TestSimple-> assign(this, data)
    }
    return rows
  }

  public fun TestSimpleTable.update(
    raw: TestSimple,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun TestSimpleTable.updateByPrimaryKey(raw: TestSimple, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(id)) {
    TestSimpleTable.id.eq(raw.id)
  }

  public fun TestSimpleTable.updateByPrimaryKey(id: String,
      builder: TestSimpleTable.(UpdateStatement) -> Unit): Int = update({ TestSimpleTable.id.eq(id)
      }, body = builder)

  public fun TestSimpleTable.deleteByPrimaryKey(id: String): Int = deleteWhere {
    TestSimpleTable.id.eq(id)
  }

  public fun TestSimpleTable.selectByPrimaryKey(id: String, vararg selective: Column<*>):
      TestSimple? {
    val query = selectSlice(*selective).andWhere {
      TestSimpleTable.id.eq(id)
    }
    return query.firstOrNull()?.toTestSimple(*selective)
  }

  public fun TestSimpleTable.selectByPrimaryKeys(ids: Iterable<String>, vararg
      selective: Column<*>): List<TestSimple> {
    val query = selectSlice(*selective).andWhere {
      TestSimpleTable.id inList ids
    }
    return query.toTestSimpleList(*selective)
  }

  public fun TestSimpleTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Query?):
      List<TestSimple> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toTestSimpleList(*selective)
  }

  public fun TestSimpleTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Query?):
      TestSimple? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toTestSimple(*selective)
  }

  public fun TestSimpleTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<TestSimple> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      val keyValue = parseColumnValue(it, id)
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less keyValue }
        else-> query.andWhere { id greater keyValue }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(id, order)
    val list = sorted.limit(pageSize +
        1).toTestSimpleList(*selective.toTypedArray()).toMutableList()
    val dataCount = list.size
    val token = if(dataCount > pageSize) {
      list.removeLast()
      val idString = list.last().getColumnValueString(id)
      val idArray = idString.toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(idArray)
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
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<TestSimple> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val sortColAndId = forwardToken?.let { if(it.isNotBlank())
        Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) else null }
    val kp = sortColAndId?.split(":::")
    val offsetKey = if(!kp.isNullOrEmpty()) parseColumnValue(kp.first(), sortColumn) else null
    val lastId = if(kp != null && kp.size > 1 && kp[1].isNotBlank()) parseColumnValue(kp[1], id)
        else null
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { sortColumn lessEq it }
        else-> query.andWhere { sortColumn greaterEq it }
      }
    }
    lastId?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less it }
        else-> query.andWhere { id greater it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(Pair(sortColumn, order), Pair(id, order))
    val list = sorted.limit(pageSize +
        1).toTestSimpleList(*selective.toTypedArray()).toMutableList()
    val dataCount = list.size
    val token = if(dataCount > pageSize) {
      list.removeLast()
      val idToEncode = list.last().getColumnValueString(id)
      val sortKey = list.last().getColumnValueString(sortColumn)
      val tokenValue = """${idToEncode}:::${sortKey}""".toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(tokenValue)
    }
    else null
    return OffsetList(list, token)
  }

  public fun TestSimpleTable.replace(raw: TestSimple): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
