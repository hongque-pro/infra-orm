package com.labijie.infra.orm.configuration

import com.labijie.infra.orm.SimpleTableScanner
import org.slf4j.LoggerFactory
import org.springframework.aot.AotDetector
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.util.StringUtils
import java.beans.Introspector


//class TableScanner(registry: BeanDefinitionRegistry, private val excludeClasses: String? = null) :
//    ClassPathBeanDefinitionScanner(registry) {
//
//    val excludeClassNames: Set<String> = if(excludeClasses.isNullOrBlank()) setOf() else StringUtils.commaDelimitedListToSet(excludeClasses)
//
//    init {
//        this.addIncludeFilter(AssignableTypeFilter(Table::class.java))
//
//        this.addExcludeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
//            val className = metadataReader.classMetadata.className
//            className.endsWith("package-info") || excludeClassNames.contains(className)
//        }
//    }
//
//    override fun postProcessBeanDefinition(beanDefinition: AbstractBeanDefinition, beanName: String) {
//
//        beanDefinition.instanceSupplier = Supplier<Any> {
//            Class.forName(beanDefinition.beanClassName).kotlin.objectInstance!!
//       }
//    }
//
//}

class TableScanner(
    private val beanFactory: ConfigurableListableBeanFactory,
    excludeClasses: String? = null
) : SimpleTableScanner( if (excludeClasses.isNullOrBlank()) setOf() else StringUtils.commaDelimitedListToSet(excludeClasses)) {

    companion object {
        private val scannerLogger by lazy {
            LoggerFactory.getLogger(TableScanner::class.java)
        }
    }


    fun scanAndRegister(vararg basePackages: String): Set<String> {

        val mutableSet = mutableSetOf<String>()
        for (pkg in basePackages) {
            val candidates = findCandidateComponents(pkg)
            for (candidate in candidates) {
                val className = candidate.beanClassName ?: continue
                val kClass = Class.forName(className).kotlin
                val instance = kClass.objectInstance ?: continue
                val beanName = Introspector.decapitalize(kClass.simpleName!!)
                if (!beanFactory.containsSingleton(beanName)) {
                    beanFactory.registerSingleton(beanName, instance)
                    mutableSet.add(kClass.java.name)
                }

                scannerLogger.info("Table registered: $beanName, class: ${kClass.simpleName}")
            }
        }
        return mutableSet
    }
}