package de.honoka.demo.microservice.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(MainProperties::class)
@Configuration
class MainConfig

@Suppress("ArrayInDataClass")
@ConfigurationProperties("app.gateway")
data class MainProperties(

    var whiteList: Array<String> = arrayOf()
)
