package de.honoka.demo.microservice.gateway.security

import de.honoka.demo.microservice.common.api.auth.dao.AuthRedisDao
import de.honoka.sdk.spring.starter.core.springBeanLazy
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Instant

object LoginStatusFilter : WebFilter {

    private val authRedisDao by AuthRedisDao::class.springBeanLazy

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val action = ReactiveSecurityContextHolder.getContext().doOnNext { c ->
            val auth = c.authentication as JwtAuthenticationToken
            val claims = auth.tokenAttributes
            val sub = (claims["sub"] as String).toLong()
            val jti = claims["jti"] as String
            val iat = (claims["iat"] as Instant).epochSecond
            val invalid = authRedisDao.run {
                hasLogoutId(jti) || getRevokedTokenTime(sub).let {
                    it != null && iat * 1000 < it
                }
            }
            if(invalid) {
                auth.isAuthenticated = false
            }
        }
        return action.then(chain.filter(exchange))
    }
}
