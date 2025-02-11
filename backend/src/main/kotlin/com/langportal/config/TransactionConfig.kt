package com.langportal.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import jakarta.persistence.EntityManagerFactory

@Configuration
@EnableTransactionManagement
class TransactionConfig {
    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        // Configure transaction isolation for SQLite
        transactionManager.defaultTimeout = 30
        return transactionManager
    }
}