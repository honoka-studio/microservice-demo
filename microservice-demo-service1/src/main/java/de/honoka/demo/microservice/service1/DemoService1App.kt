package de.honoka.demo.microservice.service1

import de.honoka.demo.microservice.common.config.MainBaseConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(MainBaseConfig::class)
@ConfigurationPropertiesScan
@SpringBootApplication
class DemoService1App

fun main(args: Array<String>) {
    runApplication<DemoService1App>(*args)
}
