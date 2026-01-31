package de.honoka.demo.microservice.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(SecurityBaseProperties::class)
class SecurityBaseConfig

@ConfigurationProperties("microservice-demo.security")
data class SecurityBaseProperties(

    var jwt: Jwt = Jwt()
) {

    data class Jwt(

        var issuerUri: String? = null,

        var jwkSetUri: String? = null
    )
}
