@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
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
import com.labijie.orm.dummy.TestEnum
import com.labijie.orm.dummy.pojo.IntId
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
import kotlin.Pair
import kotlin.Short
import kotlin.String
import kotlin.Unit
import kotlin.collections.Collection
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.isNotEmpty
import kotlin.collections.last
import kotlin.collections.map
import kotlin.collections.toList
import kotlin.reflect.KClass
import kotlin.text.Charsets
import kotlin.text.toByteArray
import kotlin.text.toInt
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
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
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.ReplaceStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert

/**
 * DSL support for IntIdTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.IntIdTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object IntIdDSL {
  public val IntIdTable.allColumns: Array<Column<*>> by lazy {
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

  public fun parseRow(raw: ResultRow): IntId {
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
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): IntId {
    val plain = IntId()
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

  public fun <T> IntIdTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
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
    id->Int::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'IntId'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> IntId.getColumnValue(column: Column<T>): T = when(column) {
    IntIdTable.name->this.name as T
    IntIdTable.memo->this.memo as T
    IntIdTable.charCol->this.charCol as T
    IntIdTable.textCol->this.textCol as T
    IntIdTable.enumCol->this.enumCol as T
    IntIdTable.binaryCol->this.binaryCol as T
    IntIdTable.uidCol->this.uidCol as T
    IntIdTable.shortCol->this.shortCol as T
    IntIdTable.booleanCol->this.booleanCol as T
    IntIdTable.byteCol->this.byteCol as T
    IntIdTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'IntId'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: IntId,
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

  public fun ResultRow.toIntId(vararg selective: Column<*>): IntId {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toIntIdList(vararg selective: Column<*>): List<IntId> = this.map {
    it.toIntId(*selective)
  }

  public fun IntIdTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: IntId, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: IntId, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun IntIdTable.insert(raw: IntId): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun IntIdTable.upsert(
    raw: IntId,
    onUpdate: List<Pair<Column<*>, Expression<*>>>? = null,
    onUpdateExclude: List<Column<*>>? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun IntIdTable.batchInsert(
    list: Iterable<IntId>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun IntIdTable.update(
    raw: IntId,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun IntIdTable.updateByPrimaryKey(raw: IntId, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(id)) {
    IntIdTable.id.eq(raw.id)
  }

  public fun IntIdTable.updateByPrimaryKey(id: Int, builder: IntIdTable.(UpdateStatement) -> Unit):
      Int = update({ IntIdTable.id.eq(id) }, body = builder)

  public fun IntIdTable.deleteByPrimaryKey(id: Int): Int = deleteWhere {
    IntIdTable.id.eq(id)
  }

  public fun IntIdTable.selectByPrimaryKey(id: Int, vararg selective: Column<*>): IntId? {
    val query = selectSlice(*selective).andWhere {
      IntIdTable.id.eq(id)
    }
    return query.firstOrNull()?.toIntId(*selective)
  }

  public fun IntIdTable.selectByPrimaryKeys(ids: Iterable<Int>, vararg selective: Column<*>):
      List<IntId> {
    val query = selectSlice(*selective).andWhere {
      IntIdTable.id inList ids
    }
    return query.toIntIdList(*selective)
  }

  public fun IntIdTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<IntId> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toIntIdList(*selective)
  }

  public fun IntIdTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit): IntId? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toIntId(*selective)
  }

  public fun IntIdTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<IntId> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less it.toInt() }
        else-> query.andWhere { id greater it.toInt() }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(id, order)
    val list = sorted.limit(pageSize).toIntIdList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> IntIdTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<IntId> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val kp = forwardToken?.let { decodeToken(it) }
    val offsetKey = kp?.first
    val excludeKeys = kp?.second?.map { it.toInt() }
    val query = selectSlice(*selective.toTypedArray())
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
    val list = sorted.limit(pageSize).toIntIdList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        IntId::id)
    return OffsetList(list, token)
  }

  public fun IntIdTable.replace(raw: IntId): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
