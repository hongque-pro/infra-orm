@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.orm.dummy.ShopTable
import com.labijie.orm.dummy.ShopTable.count
import com.labijie.orm.dummy.ShopTable.id
import com.labijie.orm.dummy.ShopTable.name
import com.labijie.orm.dummy.ShopTable.status
import com.labijie.orm.dummy.TestEnum
import com.labijie.orm.dummy.pojo.Shop
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
 * DSL support for ShopTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.ShopTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object ShopDSL {
  public val ShopTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    name,
    status,
    count,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): Shop {
    val plain = Shop()
    plain.name = raw[name]
    plain.status = raw[status]
    plain.count = raw[count]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): Shop {
    val plain = Shop()
    if(row.hasValue(name)) {
      plain.name = row[name]
    }
    if(row.hasValue(status)) {
      plain.status = row[status]
    }
    if(row.hasValue(count)) {
      plain.count = row[count]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> ShopTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    name->String::class
    status->TestEnum::class
    count->Int::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'Shop'""")
  }

  private fun <T> Shop.getColumnValueString(column: Column<T>): String = when(column) {
    ShopTable.name->this.name
    ShopTable.count->this.count.toString()

    ShopTable.id->this.id.toString()

    else->throw
        IllegalArgumentException("""Can ot converter value of Shop::${column.name} to string.""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  private fun <T> parseColumnValue(valueString: String, column: Column<T>): T {
    val value = when(column) {
      ShopTable.name -> valueString
      ShopTable.count ->valueString.toInt()
      ShopTable.id ->valueString.toLong()
      else->throw
          IllegalArgumentException("""Can ot converter value of Shop::${column.name} to string.""")
    }
    return value as T
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> Shop.getColumnValue(column: Column<T>): T = when(column) {
    ShopTable.name->this.name as T
    ShopTable.status->this.status as T
    ShopTable.count->this.count as T
    ShopTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'Shop'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: Shop,
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
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toShop(vararg selective: Column<*>): Shop {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toShopList(vararg selective: Column<*>): List<Shop> = this.map {
    it.toShop(*selective)
  }

  public fun ShopTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: Shop, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: Shop, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun ShopTable.insert(raw: Shop): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun ShopTable.insertIgnore(raw: Shop): InsertStatement<Long> = insertIgnore {
    assign(it, raw)
  }

  public fun ShopTable.upsert(
    raw: Shop,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun ShopTable.batchInsert(
    list: Iterable<Shop>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun ShopTable.batchUpsert(
    list: Iterable<Shop>,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    shouldReturnGeneratedValues: Boolean = false,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): List<ResultRow> {
    val rows =  batchUpsert(data = list, keys = arrayOf(id), onUpdate = onUpdate, onUpdateExclude =
        onUpdateExclude, where = where, shouldReturnGeneratedValues = shouldReturnGeneratedValues) {
      data: Shop-> assign(this, data)
    }
    return rows
  }

  public fun ShopTable.update(
    raw: Shop,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun ShopTable.updateByPrimaryKey(raw: Shop, vararg selective: Column<*>): Int = update(raw,
      selective = selective, ignore = arrayOf(id)) {
    ShopTable.id.eq(raw.id)
  }

  public fun ShopTable.updateByPrimaryKey(id: Long, builder: ShopTable.(UpdateStatement) -> Unit):
      Int = update({ ShopTable.id.eq(id) }, body = builder)

  public fun ShopTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    ShopTable.id.eq(id)
  }

  public fun ShopTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): Shop? {
    val query = selectSlice(*selective).andWhere {
      ShopTable.id.eq(id)
    }
    return query.firstOrNull()?.toShop(*selective)
  }

  public fun ShopTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<Shop> {
    val query = selectSlice(*selective).andWhere {
      ShopTable.id inList ids
    }
    return query.toShopList(*selective)
  }

  public fun ShopTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Query?):
      List<Shop> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toShopList(*selective)
  }

  public fun ShopTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Query?): Shop? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toShop(*selective)
  }

  public fun ShopTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<Shop> {
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
    val list = sorted.limit(pageSize + 1).toShopList(*selective.toTypedArray()).toMutableList()
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

  public fun <T : Comparable<T>> ShopTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<Shop> {
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
    val list = sorted.limit(pageSize + 1).toShopList(*selective.toTypedArray()).toMutableList()
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

  public fun ShopTable.replace(raw: Shop): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
