package de.honoka.demo.microservice.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@Configuration
class SecurityConfig(private val mainProperties: MainProperties) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http.run {
        authorizeExchange {
            it.pathMatchers(*mainProperties.whiteList).permitAll()
            it.anyExchange().authenticated()
        }
        oauth2ResourceServer {
            it.jwt(Customizer.withDefaults())
        }
        csrf {
            it.disable()
        }
        build()
    }
}
