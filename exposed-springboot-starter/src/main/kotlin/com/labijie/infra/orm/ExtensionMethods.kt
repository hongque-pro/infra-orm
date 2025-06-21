/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm

import java.net.URL
import java.util.Enumeration
import java.util.Properties


fun <T : Any> withoutSqlLog(action: () -> T?): T? {
    SqlLoggerSettings.apply(false)
    try {
        return action()
    } finally {
        SqlLoggerSettings.reset()
    }
}

fun <T : Any> withSqlLog(action: () -> T?): T? {
    SqlLoggerSettings.apply(true)
    try {
        return action()
    } finally {
        SqlLoggerSettings.reset()
    }
}


fun getOrmGitInfo(): Properties {
    val systemResources: Enumeration<URL> =
        (SimpleTableScanner::class.java.classLoader
            ?: ClassLoader.getSystemClassLoader()).getResources("git-info/git.properties")
    while (systemResources.hasMoreElements()) {
        systemResources.nextElement().openStream().use { stream ->
            val properties = Properties().apply {
                this.load(stream)
            }.let {
                if (it.getProperty("project.group") == "com.labijie.orm" &&
                    it.getProperty("project.name") == "exposed-springboot-starter"
                ) {
                    it
                } else null
            }
            if (properties != null) {
                return properties
            }
        }
    }
    return Properties()
}