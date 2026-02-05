package de.honoka.demo.microservice.auth.security

import de.honoka.demo.microservice.common.api.user.stub.UserControllerStub
import de.honoka.sdk.spring.starter.security.token.BasicAuthenticationToken
import de.honoka.sdk.util.kotlin.text.toJsonArray
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.stereotype.Component

/**
 * JWT内容的额外配置
 */
@Component
class TokenCustomizer(private val userControllerStub: UserControllerStub) : OAuth2TokenCustomizer<JwtEncodingContext> {

    override fun customize(context: JwtEncodingContext) {
        if(context.tokenType != OAuth2TokenType.ACCESS_TOKEN) return
        val token = context.getPrincipal<BasicAuthenticationToken>()
        val user = userControllerStub.queryById(token.userId).data!!
        context.claims.claim("authorities", user.authorities!!.toJsonArray())
    }
}
