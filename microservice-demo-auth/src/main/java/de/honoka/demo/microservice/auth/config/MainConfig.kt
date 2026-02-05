package de.honoka.demo.microservice.auth.config

import de.honoka.demo.microservice.common.config.CommonBaseConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(CommonBaseConfig::class)
@EnableConfigurationProperties(MainProperties::class)
@Configuration
class MainConfig

@ConfigurationProperties("microservice-demo.auth")
data class MainProperties(

    var keyId: String? = null,

    var keyPath: String? = null,

    var jwt: Jwt = Jwt()
) {

    data class Jwt(

        var issuerUri: String? = null,

        var jwkSetUri: String? = null
    )
}
