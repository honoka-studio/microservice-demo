package de.honoka.demo.microservice.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import de.honoka.demo.microservice.auth.security.CustomLoginStatusFilter
import de.honoka.sdk.spring.starter.security.DefaultAccessDeniedHandler
import de.honoka.sdk.spring.starter.security.DefaultAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.SecurityContextHolderFilter
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

@EnableWebSecurity
@Configuration
class SecurityConfig {

    /**
     * OAuth2的相关配置
     */
    @Bean
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain = http.run {
        authorizeHttpRequests {
            it.anyRequest().authenticated()
        }
        /*
         * OAuth2AuthorizationServerConfigurer中有一个名为endpointsMatcher的局部变量，其包含了项目中定义
         * 的AuthorizationServerSettings所配置的OAuth2相关的几个接口的路径，如/oauth2/token等。
         * endpointsMatcher在默认配置中被传递给了securityMatcher方法，该方法用于设置http对象所包含的配置
         * 要作用于哪些请求路径。即此处表示该http对象所包含的配置仅作用于OAuth2相关的几个接口。
         * 同时，endpointsMatcher也传递给了csrf配置的ignoringRequestMatchers方法，不对这些接口进行csrf防护。
         */
        with(OAuth2AuthorizationServerConfigurer.authorizationServer()) { o ->
            securityMatcher(o.endpointsMatcher)
            csrf {
                it.ignoringRequestMatchers(o.endpointsMatcher)
            }
            o.authorizationEndpoint {
                /*
                 * 设置自定义授权确认页面路径（可以为任意路径，通常推荐为/oauth2/consent）
                 *
                 * 此路径必须设置，否则Spring Security会在客户端请求/oauth2/authorize时直接返回一段html，
                 * 难以获取state等参数。
                 * /oauth2/consent这个路径本身不存在，理论上是由开发者自行实现，但也可以不实现，而是在调用
                 * /oauth2/authorize得到301响应后，直接根据要重定向的URL，拿到其中的state值。
                 */
                it.consentPage("/oauth2/consent")
            }
        }
        //添加能够识别自定义登录态，并将其放入SecurityContextHolder中的处理器
        //不可用OAuth2相关Filter来确定要添加的Filter所处的位置，因为OAuth2相关Filter在调用build时才会被添加
        addFilterAfter(CustomLoginStatusFilter, SecurityContextHolderFilter::class.java)
        oauth2ResourceServer {
            //使用JWT处理接收到的Access Token
            it.jwt(Customizer.withDefaults())
        }
        //设置自定义的Security异常处理器，用于处理未登录、无权访问等情况
        exceptionHandling {
            /*
             * 定义认证入口点，当AuthorizationFilter检测到SecurityContextHolder的context中没有
             * authentication信息时，则调用此处配置的authenticationEntryPoint中的方法，来执行开发者
             * 定义的后续行为。
             */
            it.authenticationEntryPoint(DefaultAuthenticationEntryPoint)
            it.accessDeniedHandler(DefaultAccessDeniedHandler)
        }
        build()
    }

    @Bean
    fun registeredClientRepository(jdbcTemplate: JdbcTemplate): RegisteredClientRepository = run {
        JdbcRegisteredClientRepository(jdbcTemplate)
    }

    //JWK相关资料：https://datatracker.ietf.org/doc/html/draft-ietf-jose-json-web-key-41
    /**
     * 配置JWK，为JWT提供加密密钥，用于加密、解密或签名、验签
     */
    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = KeyPairGenerator.getInstance("RSA").run {
            initialize(2048)
            generateKeyPair()
        }
        val rsaKey = RSAKey.Builder(keyPair.public as RSAPublicKey).run {
            privateKey(keyPair.private as RSAPrivateKey)
            keyID(UUID.randomUUID().toString())
            build()
        }
        return ImmutableJWKSet(JWKSet(rsaKey))
    }

    /**
     * 配置JWT解析器
     */
    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder = run {
        OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    /**
     * 配置授权服务器请求地址（与OAuth2相关的一些请求地址，默认为/oauth2/token等）
     */
    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings = AuthorizationServerSettings.builder().run {
        //不作配置，使用默认地址
        build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
