/**
 * @author Anders Xiao
 * @date 2024-07-26
 */
package com.labbijie.infra.orm.testing

import com.labijie.infra.orm.ExposedUtils
import kotlin.test.Test
import kotlin.test.assertEquals


class InfraGitPropertiesTester {

    @Test
    fun testGetGitProperties() {
        val properties = ExposedUtils.getInfraOrmGitProperties()
        assertEquals ("com.labijie.orm", properties.getProperty("project.group"))
        assertEquals ("exposed-core", properties.getProperty("project.name"))
    }
}