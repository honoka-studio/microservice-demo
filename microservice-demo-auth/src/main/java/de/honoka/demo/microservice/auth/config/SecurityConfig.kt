package de.honoka.demo.microservice.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import de.honoka.demo.microservice.auth.security.LoginStatusFilter
import de.honoka.demo.microservice.common.config.SecurityBaseConfig
import de.honoka.demo.microservice.common.config.SecurityBaseProperties
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.sdk.spring.starter.security.DefaultAccessDeniedHandler
import de.honoka.sdk.spring.starter.security.DefaultAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.SecurityContextHolderFilter
import java.io.File

@EnableWebSecurity
@Import(SecurityBaseConfig::class)
@Configuration
class SecurityConfig(
    private val mainProperties: MainProperties,
    private val securityBaseProperties: SecurityBaseProperties
) {

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
        }
        //添加能够识别自定义登录态，并将其放入SecurityContextHolder中的处理器
        //不可用OAuth2相关Filter来确定要添加的Filter所处的位置，因为OAuth2相关Filter在调用build时才会被添加
        addFilterAfter(LoginStatusFilter, SecurityContextHolderFilter::class.java)
        oauth2ResourceServer {
            //使用JWT处理接收到的Access Token
            it.jwt(Customizer.withDefaults())
            /*
             * 定义用于OAuth2相关的Filter的异常处理逻辑
             * （此处的配置仅作用于OAuth2相关过滤器，不作用于AuthorizationFilter）
             */
            it.authenticationEntryPoint(DefaultAuthenticationEntryPoint)
            it.accessDeniedHandler(DefaultAccessDeniedHandler)
        }
        /*
         * 设置自定义的Security异常处理器，用于处理未登录、无权访问等情况
         * （此处的配置仅作用于AuthorizationFilter）
         */
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
    fun registeredClientRepository(jdbcTemplate: JdbcTemplate): RegisteredClientRepository =
        JdbcRegisteredClientRepository(jdbcTemplate)

    //JWK相关资料：https://datatracker.ietf.org/doc/html/draft-ietf-jose-json-web-key-41
    /**
     * 配置JWK，为JWT提供加密密钥，用于加密、解密或签名、验签
     */
    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyString = File(mainProperties.keyPath!!).readText()
        val key = RSAKey.parseFromPEMEncodedObjects(keyString).run {
            RSAKey.Builder(toRSAKey()).run {
                keyID(mainProperties.keyId!!)
                build()
            }
        }
        return ImmutableJWKSet(JWKSet(key))
    }

    /**
     * 配置JWT解析器
     */
    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder =
        OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)

    /**
     * 授权服务器配置
     *
     * 可配置：请求地址（与OAuth2相关的一些请求地址，默认为/oauth2/token等），JWT签发者（iss）等
     */
    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings = AuthorizationServerSettings.builder().run {
        issuer(securityBaseProperties.jwt.issuerUri)
        build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = SecurityUtils.passwordEncoder
}
