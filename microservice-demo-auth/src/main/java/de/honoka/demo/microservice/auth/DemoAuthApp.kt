package de.honoka.demo.microservice.auth

import de.honoka.demo.microservice.common.config.MainBaseConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(MainBaseConfig::class)
@ConfigurationPropertiesScan
@SpringBootApplication
class DemoAuthApp

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

fun main(args: Array<String>) {
    runApplication<DemoAuthApp>(*args)
}
