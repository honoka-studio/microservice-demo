package de.honoka.demo.microservice.gateway

import de.honoka.demo.microservice.common.config.MainBaseConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(MainBaseConfig::class)
@ConfigurationPropertiesScan
@SpringBootApplication
class DemoGatewayApp

fun main(args: Array<String>) {
    runApplication<DemoGatewayApp>(*args)
}
