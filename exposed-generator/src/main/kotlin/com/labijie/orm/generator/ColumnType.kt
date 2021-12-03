package com.labijie.orm.generator

import com.google.devtools.ksp.symbol.KSType

data class ColumnType(val rawType: KSType, val type: KSType, val isNullable: Boolean)