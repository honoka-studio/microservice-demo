package de.honoka.demo.microservice.user

import de.honoka.demo.microservice.common.config.MainBaseConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(MainBaseConfig::class)
@ConfigurationPropertiesScan
@SpringBootApplication
class DemoUserApp

fun main(args: Array<String>) {
    runApplication<DemoUserApp>(*args)
}
