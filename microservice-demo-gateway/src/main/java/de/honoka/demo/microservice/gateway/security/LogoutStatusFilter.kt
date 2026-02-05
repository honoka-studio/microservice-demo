package de.honoka.demo.microservice.gateway.security

import de.honoka.demo.microservice.common.api.auth.dao.AuthRedisDao
import de.honoka.sdk.spring.starter.core.springBean
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

object LogoutStatusFilter : WebFilter {

    private val authRedisDao by lazy { AuthRedisDao::class.springBean }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val action = ReactiveSecurityContextHolder.getContext().doOnNext {
            val auth = it.authentication as JwtAuthenticationToken
            val jti = auth.tokenAttributes["jti"] as String
            if(authRedisDao.hasLogoutId(jti)) {
                auth.isAuthenticated = false
            }
        }
        return action.then(chain.filter(exchange))
    }
}
