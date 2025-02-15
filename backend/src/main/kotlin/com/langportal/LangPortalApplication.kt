package com.langportal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LangPortalApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<LangPortalApplication>(*args)
        }
    }
}