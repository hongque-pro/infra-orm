package com.labijie.infra.orm.configuration

import org.springframework.beans.PropertyValues
import org.springframework.beans.factory.BeanNameAware
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.PropertyResourceConfigurer
import org.springframework.beans.factory.config.TypedStringValue
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.util.StringUtils


class TableDefinitionPostProcessor : BeanDefinitionRegistryPostProcessor, ApplicationContextAware, BeanNameAware {

    private lateinit var context: ApplicationContext
    private var beanName: String = ""

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {

    }

    var packages: String = ""
    var processPropertyPlaceHolders = false
    var excludeClasses: String = ""

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {

        if (this.processPropertyPlaceHolders) {
            processPropertyPlaceHolders()
        }



        val beanFactory = (context as ConfigurableApplicationContext).beanFactory

        val scanner = TableScanner(beanFactory, excludeClasses)
        scanner.resourceLoader = context

        if(packages.isNotBlank()) {
            val packagesArray = StringUtils.tokenizeToStringArray(packages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS)
            scanner.scanAndRegister(*packagesArray)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    /*
   * BeanDefinitionRegistries are called early in application startup, before BeanFactoryPostProcessors. This means that
   * PropertyResourceConfigurers will not have been loaded and any property substitution of this class' properties will
   * fail. To avoid this, find any PropertyResourceConfigurers defined in the context and run them on this class' bean
   * definition. Then update the values.
   */
    private fun processPropertyPlaceHolders(): BeanDefinition? {
        val prcs: Map<String, PropertyResourceConfigurer> = this.context.getBeansOfType(
            PropertyResourceConfigurer::class.java,
            false, false
        )
        if (prcs.isNotEmpty() && context is ConfigurableApplicationContext) {
            val mapperScannerBean = (context as ConfigurableApplicationContext).beanFactory
                .getBeanDefinition(beanName)

            // PropertyResourceConfigurer does not expose any methods to explicitly perform
            // property placeholder substitution. Instead, create a BeanFactory that just
            // contains this mapper scanner and post process the factory.
            val factory = DefaultListableBeanFactory()
            factory.registerBeanDefinition(beanName, mapperScannerBean)
            for (prc in prcs.values) {
                prc.postProcessBeanFactory(factory)
            }


            val values: PropertyValues = mapperScannerBean.propertyValues
            this.packages = getPropertyValue(this::packages.name, values) ?: ""
            this.excludeClasses = getPropertyValue(this::excludeClasses.name, values) ?: ""
            return mapperScannerBean
        }
        return null
    }

    private fun getPropertyValue(propertyName: String, values: PropertyValues): String? {
        val property = values.getPropertyValue(propertyName) ?: return null
        val value = property.value
        return if (value == null) {
            null
        } else (value as? String)
            ?: if (value is TypedStringValue) {
                value.value
            } else {
                null
            }
    }

    override fun setBeanName(name: String) {
        this.beanName = name
    }
}