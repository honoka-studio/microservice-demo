package de.honoka.demo.microservice.gateway.config

import de.honoka.demo.microservice.gateway.security.LogoutStatusFilter
import de.honoka.sdk.spring.starter.config.WebFluxSecurityProperties
import de.honoka.sdk.spring.starter.security.webflux.DefaultServerAccessDeniedHandler
import de.honoka.sdk.spring.starter.security.webflux.DefaultServerAuthenticationEntryPoint
import de.honoka.sdk.spring.starter.security.webflux.hasWildcardAuthority
import de.honoka.sdk.spring.starter.security.webflux.token.JwtReactiveUtils
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
@EnableConfigurationProperties(WebFluxSecurityProperties::class)
@Configuration
class SecurityConfig(private val webFluxSecurityProperties: WebFluxSecurityProperties) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http.run {
        authorizeExchange {
            it.pathMatchers(*webFluxSecurityProperties.whiteList.toTypedArray()).permitAll()
            webFluxSecurityProperties.authorities.forEach { e ->
                it.pathMatchers(*e.paths.toTypedArray()).hasWildcardAuthority(e.name!!)
            }
            it.anyExchange().authenticated()
        }
        addFilterAfter(LogoutStatusFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        oauth2ResourceServer {
            it.jwt { j ->
                j.jwtAuthenticationConverter(JwtReactiveUtils.newJwtAuthenticationConverter())
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
