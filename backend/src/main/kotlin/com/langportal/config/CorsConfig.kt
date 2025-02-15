package com.langportal.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:5173") // React dev server
        config.addAllowedOrigin("http://localhost:3000") // Alternative React port
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}