package de.honoka.demo.microservice.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(MainProperties::class)
@Configuration
class MainConfig

@ConfigurationProperties("app.auth")
data class MainProperties(

    var keyId: String? = null,

    var keyPath: String? = null
)
