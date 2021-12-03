package com.labijie.infra.orm.testing

import com.labijie.orm.generator.findProjectSourceDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FolderPathTester {
    @Test
    fun testPath(){
        val path = "E:\\Work\\infra-orm\\dummy-project\\src\\main\\kotlin\\com\\labijie\\orm\\dummy".replace("\\", File.separator)
        val result = "E:\\Work\\infra-orm\\dummy-project\\src\\main\\kotlin".replace("\\", File.separator)
        val find = findProjectSourceDir(path)
        assertEquals(result, find.toString())
    }
}