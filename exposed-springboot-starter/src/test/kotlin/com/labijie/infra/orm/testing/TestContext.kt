/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.infra.orm.testing

import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.orm.testing.tables.TestTable
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
@TableScan(basePackageClasses = [TestTable::class])
class TestingContext {

}