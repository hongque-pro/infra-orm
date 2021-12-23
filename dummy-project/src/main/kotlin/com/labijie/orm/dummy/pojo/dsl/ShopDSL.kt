package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.ShopTable
import com.labijie.orm.dummy.ShopTable.count
import com.labijie.orm.dummy.ShopTable.id
import com.labijie.orm.dummy.ShopTable.name
import com.labijie.orm.dummy.ShopTable.status
import com.labijie.orm.dummy.pojo.Shop
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

/**
 * DSL support for ShopTable
 *
 * This class made by a code generator (https://github.com/hongque-pro/infra-orm).
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.ShopTable
 */
public object ShopDSL {
  public fun parseShopRow(raw: ResultRow): Shop {
    val plain = Shop()
    plain.name = raw[name]
    plain.status = raw[status]
    plain.count = raw[count]
    plain.id = raw[id]
    return plain
  }

  public fun applyShop(statement: UpdateBuilder<*>, raw: Shop): Unit {
    statement[name] = raw.name
    statement[status] = raw.status
    statement[count] = raw.count
    statement[id] = raw.id
  }

  public fun applyShop(statement: UpdateStatement, raw: Shop): Unit {
    statement[name] = raw.name
    statement[status] = raw.status
    statement[count] = raw.count
    statement[id] = raw.id
  }

  public fun ResultRow.toShop(): Shop = parseShopRow(this)

  public fun Iterable<ResultRow>.toShopList(): List<Shop> = this.map(ShopDSL::parseShopRow)

  public fun UpdateBuilder<*>.apply(raw: Shop) = applyShop(this, raw)

  public fun UpdateStatement.apply(raw: Shop) = applyShop(this, raw)

  public fun ShopTable.insert(raw: Shop): InsertStatement<Number> = ShopTable.insert {
    applyShop(it, raw)
  }

  public fun ShopTable.batchInsert(list: Iterable<Shop>): List<ResultRow> {
    val rows = ShopTable.batchInsert(list) {
      entry -> applyShop(this, entry)
    }
    return rows
  }

  public fun ShopTable.update(
    raw: Shop,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>
  ): Int = ShopTable.update(where, limit) {
    applyShop(it, raw)
  }

  public fun ShopTable.update(raw: Shop): Int = ShopTable.update(raw) {
    ShopTable.id eq id
  }

  public fun ShopTable.deleteByPrimaryKey(id: Long): Int = ShopTable.deleteWhere {
    ShopTable.id eq id
  }

  public fun ShopTable.selectByPrimaryKey(id: Long): Shop? {
    val query = ShopTable.select {
      ShopTable.id eq id
    }
    return query.firstOrNull()?.toShop()
  }
}
