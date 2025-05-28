@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.orm.dummy.DurationIdEntityTable
import com.labijie.orm.dummy.DurationIdEntityTable.id
import com.labijie.orm.dummy.DurationIdEntityTable.name
import com.labijie.orm.dummy.pojo.DurationIdEntity
import java.lang.IllegalArgumentException
import java.time.Duration
import java.util.Base64
import kotlin.Array
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Long
import kotlin.Number
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
 * DSL support for DurationIdEntityTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.DurationIdEntityTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object DurationIdEntityDSL {
  public val DurationIdEntityTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    name,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): DurationIdEntity {
    val plain = DurationIdEntity()
    plain.name = raw[name]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): DurationIdEntity {
    val plain = DurationIdEntity()
    if(row.hasValue(name)) {
      plain.name = row[name]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> DurationIdEntityTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    name->String::class
    id->Duration::class
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'DurationIdEntity'""")
  }

  private fun <T> DurationIdEntity.getColumnValueString(column: Column<T>): String = when(column) {
    DurationIdEntityTable.name->this.name
    DurationIdEntityTable.id->com.labijie.infra.orm.ExposedConverter.durationToString(this.id)

    else->throw
        IllegalArgumentException("""Can ot converter value of DurationIdEntity::${column.name} to string.""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  private fun <T> parseColumnValue(valueString: String, column: Column<T>): T {
    val value = when(column) {
      DurationIdEntityTable.name -> valueString
      DurationIdEntityTable.id ->com.labijie.infra.orm.ExposedConverter.stringToDuration(valueString)
      else->throw
          IllegalArgumentException("""Can ot converter value of DurationIdEntity::${column.name} to string.""")
    }
    return value as T
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> DurationIdEntity.getColumnValue(column: Column<T>): T = when(column) {
    DurationIdEntityTable.name->this.name as T
    DurationIdEntityTable.id->this.id as T
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'DurationIdEntity'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: DurationIdEntity,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(name)) && !ignore.contains(name))
      builder[name] = raw.name
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toDurationIdEntity(vararg selective: Column<*>): DurationIdEntity {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toDurationIdEntityList(vararg selective: Column<*>):
      List<DurationIdEntity> = this.map {
    it.toDurationIdEntity(*selective)
  }

  public fun DurationIdEntityTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: DurationIdEntity, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: DurationIdEntity, vararg selective: Column<*>):
      Unit = assign(this, raw, selective = selective)

  public fun DurationIdEntityTable.insert(raw: DurationIdEntity): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun DurationIdEntityTable.insertIgnore(raw: DurationIdEntity): InsertStatement<Long> =
      insertIgnore {
    assign(it, raw)
  }

  public fun DurationIdEntityTable.upsert(
    raw: DurationIdEntity,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun DurationIdEntityTable.batchInsert(
    list: Iterable<DurationIdEntity>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun DurationIdEntityTable.batchUpsert(
    list: Iterable<DurationIdEntity>,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    shouldReturnGeneratedValues: Boolean = false,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): List<ResultRow> {
    val rows =  batchUpsert(data = list, keys = arrayOf(id), onUpdate = onUpdate, onUpdateExclude =
        onUpdateExclude, where = where, shouldReturnGeneratedValues = shouldReturnGeneratedValues) {
      data: DurationIdEntity-> assign(this, data)
    }
    return rows
  }

  public fun DurationIdEntityTable.update(
    raw: DurationIdEntity,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun DurationIdEntityTable.updateByPrimaryKey(raw: DurationIdEntity, vararg
      selective: Column<*>): Int = update(raw, selective = selective, ignore = arrayOf(id)) {
    DurationIdEntityTable.id.eq(raw.id)
  }

  public fun DurationIdEntityTable.updateByPrimaryKey(id: Duration,
      builder: DurationIdEntityTable.(UpdateStatement) -> Unit): Int = update({
      DurationIdEntityTable.id.eq(id) }, body = builder)

  public fun DurationIdEntityTable.deleteByPrimaryKey(id: Duration): Int = deleteWhere {
    DurationIdEntityTable.id.eq(id)
  }

  public fun DurationIdEntityTable.selectByPrimaryKey(id: Duration, vararg selective: Column<*>):
      DurationIdEntity? {
    val query = selectSlice(*selective).andWhere {
      DurationIdEntityTable.id.eq(id)
    }
    return query.firstOrNull()?.toDurationIdEntity(*selective)
  }

  public fun DurationIdEntityTable.selectByPrimaryKeys(ids: Iterable<Duration>, vararg
      selective: Column<*>): List<DurationIdEntity> {
    val query = selectSlice(*selective).andWhere {
      DurationIdEntityTable.id inList ids
    }
    return query.toDurationIdEntityList(*selective)
  }

  public fun DurationIdEntityTable.selectMany(vararg selective: Column<*>,
      `where`: Query.() -> Query?): List<DurationIdEntity> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toDurationIdEntityList(*selective)
  }

  public fun DurationIdEntityTable.selectOne(vararg selective: Column<*>,
      `where`: Query.() -> Query?): DurationIdEntity? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toDurationIdEntity(*selective)
  }

  public fun DurationIdEntityTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<DurationIdEntity> {
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
        1).toDurationIdEntityList(*selective.toTypedArray()).toMutableList()
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

  public fun <T : Comparable<T>> DurationIdEntityTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<DurationIdEntity> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val kp = forwardToken?.let { if(it.isNotBlank()) OffsetList.decodeToken(it) else null }
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
        1).toDurationIdEntityList(*selective.toTypedArray()).toMutableList()
    val dataCount = list.size
    val token = if(dataCount > pageSize) {
      list.removeLast()
      val idToEncode = list.last().getColumnValueString(id)
      val sortKey = list.last().getColumnValueString(sortColumn)
      OffsetList.encodeToken(arrayOf(sortKey, idToEncode))
    }
    else null
    return OffsetList(list, token)
  }

  public fun DurationIdEntityTable.replace(raw: DurationIdEntity): ReplaceStatement<Long> =
      replace {
    assign(it, raw)
  }
}
