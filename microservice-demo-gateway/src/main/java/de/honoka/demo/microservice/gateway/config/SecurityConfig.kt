package de.honoka.demo.microservice.gateway.config

import de.honoka.demo.microservice.gateway.security.LoginStatusFilter
import de.honoka.sdk.spring.starter.config.WebFluxSecurityProperties
import de.honoka.sdk.spring.starter.security.webflux.DefaultServerAccessDeniedHandler
import de.honoka.sdk.spring.starter.security.webflux.DefaultServerAuthenticationEntryPoint
import de.honoka.sdk.spring.starter.security.webflux.hasWildcardAuthority
import de.honoka.sdk.spring.starter.security.webflux.token.ReactiveJwtUtils
import de.honoka.sdk.util.kotlin.text.isNotBlank
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@Configuration
class SecurityConfig(private val webFluxSecurityProperties: WebFluxSecurityProperties) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http.run {
        authorizeExchange {
            it.pathMatchers(*webFluxSecurityProperties.whiteList.toTypedArray()).permitAll()
            webFluxSecurityProperties.authorities.forEach { e ->
                it.pathMatchers(*e.paths.toTypedArray()).run {
                    when {
                        e.role.isNotBlank() -> hasRole(e.role)
                        e.permission.isNotBlank() -> hasWildcardAuthority(e.permission!!)
                    }
                }
            }
            it.anyExchange().authenticated()
        }
        addFilterAfter(LoginStatusFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        oauth2ResourceServer {
            it.jwt { j ->
                j.jwtAuthenticationConverter(ReactiveJwtUtils.authenticationConverter)
            }
            it.authenticationEntryPoint(DefaultServerAuthenticationEntryPoint)
            it.accessDeniedHandler(DefaultServerAccessDeniedHandler)
        }
        csrf {
            it.disable()
        }
        exceptionHandling {
            it.authenticationEntryPoint(DefaultServerAuthenticationEntryPoint)
            it.accessDeniedHandler(DefaultServerAccessDeniedHandler)
        }
        build()
    }
}
