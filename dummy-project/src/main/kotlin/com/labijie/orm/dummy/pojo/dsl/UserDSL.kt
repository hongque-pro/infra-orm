@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
import com.labijie.orm.dummy.TestEnum
import com.labijie.orm.dummy.UserTable
import com.labijie.orm.dummy.UserTable.count
import com.labijie.orm.dummy.UserTable.description
import com.labijie.orm.dummy.UserTable.id
import com.labijie.orm.dummy.UserTable.name
import com.labijie.orm.dummy.UserTable.status
import com.labijie.orm.dummy.pojo.User
import java.lang.IllegalArgumentException
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
import kotlin.collections.map
import kotlin.collections.toList
import kotlin.reflect.KClass
import kotlin.text.Charsets
import kotlin.text.toByteArray
import kotlin.text.toLong
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
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

/**
 * DSL support for UserTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.UserTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object UserDSL {
  public val UserTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    name,
    status,
    count,
    description,
    id,
    )
  }


  public fun parseRow(raw: ResultRow): User {
    val plain = User()
    plain.name = raw[name]
    plain.status = raw[status]
    plain.count = raw[count]
    plain.description = raw[description]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): User {
    val plain = User()
    if(row.hasValue(name)) {
      plain.name = row[name]
    }
    if(row.hasValue(status)) {
      plain.status = row[status]
    }
    if(row.hasValue(count)) {
      plain.count = row[count]
    }
    if(row.hasValue(description)) {
      plain.description = row[description]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> UserTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    name->String::class
    status->TestEnum::class
    count->Int::class
    description->String::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'User'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> User.getColumnValue(column: Column<T>): T = when(column) {
    UserTable.name->this.name as T
    UserTable.status->this.status as T
    UserTable.count->this.count as T
    UserTable.description->this.description as T
    UserTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'User'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: User,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(name)) && !ignore.contains(name))
      builder[name] = raw.name
    if((list == null || list.contains(status)) && !ignore.contains(status))
      builder[status] = raw.status
    if((list == null || list.contains(count)) && !ignore.contains(count))
      builder[count] = raw.count
    if((list == null || list.contains(description)) && !ignore.contains(description))
      builder[description] = raw.description
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toUser(vararg selective: Column<*>): User {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toUserList(vararg selective: Column<*>): List<User> = this.map {
    it.toUser(*selective)
  }

  public fun UserTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: User, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: User, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun UserTable.insert(raw: User): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun UserTable.batchInsert(
    list: Iterable<User>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun UserTable.update(
    raw: User,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun UserTable.updateByPrimaryKey(raw: User, vararg selective: Column<*>): Int = update(raw,
      selective = selective, ignore = arrayOf(id)) {
    UserTable.id.eq(raw.id)
  }

  public fun UserTable.updateByPrimaryKey(id: Long, builder: UserTable.(UpdateStatement) -> Unit):
      Int = update({ UserTable.id.eq(id) }, body = builder)

  public fun UserTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    UserTable.id.eq(id)
  }

  public fun UserTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): User? {
    val query = selectSlice(*selective).andWhere {
      UserTable.id.eq(id)
    }
    return query.firstOrNull()?.toUser(*selective)
  }

  public fun UserTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<User> {
    val query = selectSlice(*selective).andWhere {
      UserTable.id inList ids
    }
    return query.toUserList(*selective)
  }

  public fun UserTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<User> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toUserList(*selective)
  }

  public fun UserTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit): User? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toUser(*selective)
  }

  public fun UserTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<User> {
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
    val list = sorted.limit(pageSize).toUserList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> UserTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<User> {
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
    val list = sorted.limit(pageSize).toUserList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        User::id)
    return OffsetList(list, token)
  }
}
