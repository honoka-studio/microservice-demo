package de.honoka.demo.microservice.auth.test

import de.honoka.demo.microservice.common.util.SecurityUtils
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.time.Duration
import java.util.*

class AllTest {

    @Test
    fun test1() {
        println("test1")
    }
}

@SpringBootTest
class AllSpringTest {

    @Resource
    lateinit var registeredClientRepository: RegisteredClientRepository

    @Test
    fun test1() {
        val client = RegisteredClient.withId(UUID.randomUUID().toString()).run {
            clientId("microservice-demo-user")
            clientSecret(SecurityUtils.passwordEncoder.encode(
                "microservice-demo-user"
            ))
            /*
             * 通过/oauth2/token接口提供授权码以获取token时，使用默认的认证方式（用于校验是哪个用户在为指定的
             * 第三方应用请求token）。
             * Basic：指的是在Authorization请求头中使用“Basic {base64}”的方式请求授权码，其中base64的内容为
             * “{clientId}:{clientSecret（明文）}”的base64编码（不含大括号）。
             * Post：指的是在POST请求体中附带client_id与client_secret（明文）两个参数。
             */
            clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            //配置授权码模式，刷新令牌，客户端模式
            authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            //用户确认授权后，请求以下回调地址，并在请求参数中携带code参数（授权码）
            redirectUri("http://localhost:8082/oauth2/callback")
            //设置客户端权限范围
            scope("all")
            val clientSettings = ClientSettings.builder().run {
                /*
                 * 客户端在请求认证服务获取授权码时，需要先重定向到用于确认授权的路径（既可能是网页也可能是
                 * 接口）。Spring在返回重定向响应时，除了用于确认授权的路径，还会额外附带三个参数：
                 * client_id：OAuth2客户端名
                 * state：用于在通过POST方式请求/oauth2/authorize验证请求合法性（确保在请求之前打开过用于
                 * 确认授权的路径）
                 * scope：可供用户选择的访问内容，空格隔开
                 */
                requireAuthorizationConsent(false)
                build()
            }
            val tokenSettings = TokenSettings.builder().run {
                accessTokenTimeToLive(Duration.ofMinutes(5))
                refreshTokenTimeToLive(Duration.ofHours(1))
                build()
            }
            clientSettings(clientSettings)
            tokenSettings(tokenSettings)
            build()
        }
        registeredClientRepository.save(client)
    }
}
