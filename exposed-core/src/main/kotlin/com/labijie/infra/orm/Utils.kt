package com.labijie.infra.orm

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/28
 * @Description:
 */
fun String.toUUID(): UUID {
    return UUID.fromString(this)
}


fun Table.dropColumn(column: String): String {
    val tr = TransactionManager.current()
    val columnName = tr.db.identifierManager.quoteIdentifierWhenWrongCaseOrNecessary(column)
    return "ALTER TABLE ${tr.identity(this)} DROP COLUMN $columnName"
}


/**
 * Mutate Query instance and add `andPart` to where condition with `and` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.andWhereIf(condition:Boolean, andPart: SqlExpressionBuilder.() -> Op<Boolean>): Query {
    return if(condition) {
        this.andWhere(andPart)
    }else {
        this
    }
}

/**
 * Mutate Query instance and add `orPart` to where condition with `or` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.orWhereIf(condition:Boolean, orPart: SqlExpressionBuilder.() -> Op<Boolean>) : Query {
    return if(condition) {
        this.orWhere(orPart)
    }else {
        this
    }
}

/**
 * Mutate Query instance and add `andPart` to having condition with `and` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.andHavingIf(condition:Boolean, andPart: SqlExpressionBuilder.() -> Op<Boolean>) : Query {
    return if(condition) {
        this.andHaving(andPart)
    }else {
        this
    }
}

/**
 * Mutate Query instance and add `orPart` to having condition with `or` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.orHavingIf(condition:Boolean, orPart: SqlExpressionBuilder.() -> Op<Boolean>) : Query {
    return if(condition) {
        this.orHaving(orPart)
    }else {
        this
    }
}
