@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
import com.labijie.orm.dummy.PostTable
import com.labijie.orm.dummy.PostTable.array
import com.labijie.orm.dummy.PostTable.date
import com.labijie.orm.dummy.PostTable.dateTime
import com.labijie.orm.dummy.PostTable.description
import com.labijie.orm.dummy.PostTable.duration
import com.labijie.orm.dummy.PostTable.id
import com.labijie.orm.dummy.PostTable.status
import com.labijie.orm.dummy.PostTable.status2
import com.labijie.orm.dummy.PostTable.time
import com.labijie.orm.dummy.PostTable.timestamp
import com.labijie.orm.dummy.PostTable.title
import com.labijie.orm.dummy.TestEnum
import com.labijie.orm.dummy.otherpackage.NestedInterface
import com.labijie.orm.dummy.pojo.Post
import java.lang.IllegalArgumentException
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Base64
import kotlin.Array
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.Pair
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
import kotlin.text.toLong
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
 * DSL support for PostTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.PostTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object PostDSL {
  public val PostTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    title,
    status,
    description,
    status2,
    array,
    dateTime,
    duration,
    time,
    date,
    timestamp,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): Post {
    val plain = Post()
    plain.title = raw[title]
    plain.status = raw[status]
    plain.description = raw[description]
    plain.status2 = raw[status2]
    plain.array = raw[array]
    plain.dateTime = raw[dateTime]
    plain.duration = raw[duration]
    plain.time = raw[time]
    plain.date = raw[date]
    plain.timestamp = raw[timestamp]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): Post {
    val plain = Post()
    if(row.hasValue(title)) {
      plain.title = row[title]
    }
    if(row.hasValue(status)) {
      plain.status = row[status]
    }
    if(row.hasValue(description)) {
      plain.description = row[description]
    }
    if(row.hasValue(status2)) {
      plain.status2 = row[status2]
    }
    if(row.hasValue(array)) {
      plain.array = row[array]
    }
    if(row.hasValue(dateTime)) {
      plain.dateTime = row[dateTime]
    }
    if(row.hasValue(duration)) {
      plain.duration = row[duration]
    }
    if(row.hasValue(time)) {
      plain.time = row[time]
    }
    if(row.hasValue(date)) {
      plain.date = row[date]
    }
    if(row.hasValue(timestamp)) {
      plain.timestamp = row[timestamp]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> PostTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    title->String::class
    status->TestEnum::class
    description->String::class
    status2->NestedInterface.StatusEnum::class
    array->List<String>::class
    dateTime->LocalDateTime::class
    duration->Duration::class
    time->LocalTime::class
    date->LocalDate::class
    timestamp->Instant::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'Post'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> Post.getColumnValue(column: Column<T>): T = when(column) {
    PostTable.title->this.title as T
    PostTable.status->this.status as T
    PostTable.description->this.description as T
    PostTable.status2->this.status2 as T
    PostTable.array->this.array as T
    PostTable.dateTime->this.dateTime as T
    PostTable.duration->this.duration as T
    PostTable.time->this.time as T
    PostTable.date->this.date as T
    PostTable.timestamp->this.timestamp as T
    PostTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'Post'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: Post,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(title)) && !ignore.contains(title))
      builder[title] = raw.title
    if((list == null || list.contains(status)) && !ignore.contains(status))
      builder[status] = raw.status
    if((list == null || list.contains(description)) && !ignore.contains(description))
      builder[description] = raw.description
    if((list == null || list.contains(status2)) && !ignore.contains(status2))
      builder[status2] = raw.status2
    if((list == null || list.contains(array)) && !ignore.contains(array))
      builder[array] = raw.array
    if((list == null || list.contains(dateTime)) && !ignore.contains(dateTime))
      builder[dateTime] = raw.dateTime
    if((list == null || list.contains(duration)) && !ignore.contains(duration))
      builder[duration] = raw.duration
    if((list == null || list.contains(time)) && !ignore.contains(time))
      builder[time] = raw.time
    if((list == null || list.contains(date)) && !ignore.contains(date))
      builder[date] = raw.date
    if((list == null || list.contains(timestamp)) && !ignore.contains(timestamp))
      builder[timestamp] = raw.timestamp
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toPost(vararg selective: Column<*>): Post {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toPostList(vararg selective: Column<*>): List<Post> = this.map {
    it.toPost(*selective)
  }

  public fun PostTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: Post, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: Post, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun PostTable.insert(raw: Post): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun PostTable.upsert(
    raw: Post,
    onUpdate: List<Pair<Column<*>, Expression<*>>>? = null,
    onUpdateExclude: List<Column<*>>? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun PostTable.batchInsert(
    list: Iterable<Post>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun PostTable.update(
    raw: Post,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun PostTable.updateByPrimaryKey(raw: Post, vararg selective: Column<*>): Int = update(raw,
      selective = selective, ignore = arrayOf(id)) {
    PostTable.id.eq(raw.id)
  }

  public fun PostTable.updateByPrimaryKey(id: Long, builder: PostTable.(UpdateStatement) -> Unit):
      Int = update({ PostTable.id.eq(id) }, body = builder)

  public fun PostTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    PostTable.id.eq(id)
  }

  public fun PostTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): Post? {
    val query = selectSlice(*selective).andWhere {
      PostTable.id.eq(id)
    }
    return query.firstOrNull()?.toPost(*selective)
  }

  public fun PostTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<Post> {
    val query = selectSlice(*selective).andWhere {
      PostTable.id inList ids
    }
    return query.toPostList(*selective)
  }

  public fun PostTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<Post> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toPostList(*selective)
  }

  public fun PostTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit): Post? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toPost(*selective)
  }

  public fun PostTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<Post> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less it.toLong() }
        else-> query.andWhere { id greater it.toLong() }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(id, order)
    val list = sorted.limit(pageSize).toPostList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> PostTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<Post> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val kp = forwardToken?.let { decodeToken(it) }
    val offsetKey = kp?.first
    val excludeKeys = kp?.second?.map { it.toLong() }
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
    val list = sorted.limit(pageSize).toPostList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        Post::id)
    return OffsetList(list, token)
  }

  public fun PostTable.replace(raw: Post): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
