package de.honoka.demo.microservice.auth.security

import de.honoka.demo.microservice.common.api.user.stub.InternalUserControllerStub
import de.honoka.sdk.spring.starter.security.springAuthorities
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.stereotype.Component

/**
 * JWT内容的额外配置
 */
@Component
class TokenCustomizer(
    private val internalUserControllerStub: InternalUserControllerStub
) : OAuth2TokenCustomizer<JwtEncodingContext> {

    override fun customize(context: JwtEncodingContext) {
        if(context.tokenType != OAuth2TokenType.ACCESS_TOKEN) return
        val token = context.getPrincipal<UsernamePasswordAuthenticationToken>()
        val user = internalUserControllerStub.queryById(token.principal as Long)
        context.claims.claim("authorities", user!!.springAuthorities)
    }
}
