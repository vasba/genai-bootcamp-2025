package com.langportal.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
class DatabaseConfig {
    @Bean
    fun dataSource(): DataSource =
        DataSourceBuilder
            .create()
            .driverClassName("org.sqlite.JDBC")
            .url("jdbc:sqlite:langportal.db")
            .build()

    @Bean
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("com.langportal.model")

        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setDatabasePlatform("org.hibernate.community.dialect.SQLiteDialect")
        em.jpaVendorAdapter = vendorAdapter

        val properties = HashMap<String, Any>()
        properties["hibernate.hbm2ddl.auto"] = "none"
        properties["hibernate.show_sql"] = "true"
        properties["hibernate.format_sql"] = "true"
        em.setJpaPropertyMap(properties)

        return em
    }

    @Bean
    fun dataSourceInitializer(dataSource: DataSource): DataSourceInitializer {
        val resourceDatabasePopulator = ResourceDatabasePopulator()
        resourceDatabasePopulator.addScript(ClassPathResource("schema.sql"))
        resourceDatabasePopulator.addScript(ClassPathResource("data.sql"))

        val dataSourceInitializer = DataSourceInitializer()
        dataSourceInitializer.setDataSource(dataSource)
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator)
        return dataSourceInitializer
    }
}
