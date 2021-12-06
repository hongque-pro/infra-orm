package com.labijie.orm.dummy.pojo.dsl

import com.labijie.orm.dummy.UserTable
import com.labijie.orm.dummy.UserTable.count
import com.labijie.orm.dummy.UserTable.id
import com.labijie.orm.dummy.UserTable.name
import com.labijie.orm.dummy.UserTable.status
import com.labijie.orm.dummy.pojo.User
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
 * DSL support for UserTable
 *
 * This class made by a code generator (https://github.com/hongque-pro/infra-orm).
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.UserTable
 */
public object UserDSL {
  public fun parseUserRow(raw: ResultRow): User {
    val plain = User()
    plain.name = raw[name]
    plain.status = raw[status]
    plain.count = raw[count]
    plain.id = raw[id]
    return plain
  }

  public fun applyUser(statement: UpdateBuilder<*>, raw: User): Unit {
    statement[name] = raw.name
    statement[status] = raw.status
    statement[count] = raw.count
    statement[id] = raw.id
  }

  public fun applyUser(statement: UpdateStatement, raw: User): Unit {
    statement[name] = raw.name
    statement[status] = raw.status
    statement[count] = raw.count
    statement[id] = raw.id
  }

  public fun ResultRow.toUser(): User = parseUserRow(this)

  public fun Iterable<ResultRow>.toUserList(): List<User> = this.map(UserDSL::parseUserRow)

  public fun UpdateBuilder<*>.apply(raw: User) = applyUser(this, raw)

  public fun UpdateStatement.apply(raw: User) = applyUser(this, raw)

  public fun UserTable.insert(raw: User): InsertStatement<Number> = UserTable.insert {
    applyUser(it, raw)
  }

  public fun UserTable.batchInsert(list: Iterable<User>): List<ResultRow> {
    val rows = UserTable.batchInsert(list) {
      entry -> applyUser(this, entry)
    }
    return rows
  }

  public fun UserTable.update(
    raw: User,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>
  ): Int = UserTable.update(where, limit) {
    applyUser(it, raw)
  }

  public fun UserTable.update(raw: User): Int = UserTable.update(raw) {
    UserTable.id eq id
  }

  public fun UserTable.deleteByPrimaryKey(id: Long): Int = UserTable.deleteWhere {
    UserTable.id eq id
  }

  public fun UserTable.selectByPrimaryKey(id: Long): User? {
    val query = UserTable.select {
      UserTable.id eq id
    }
    return query.firstOrNull()?.toUser()
  }
}
