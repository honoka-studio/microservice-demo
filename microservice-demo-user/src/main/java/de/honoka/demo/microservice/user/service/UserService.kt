package de.honoka.demo.microservice.user.service

import cn.hutool.core.codec.Base64
import cn.hutool.core.lang.UUID
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import de.honoka.demo.microservice.common.api.auth.stub.OAuth2Stub
import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.demo.microservice.common.api.user.data.UserRegisterRequest
import de.honoka.demo.microservice.common.api.user.data.UserRegisterResponse
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.demo.microservice.user.mapper.UserMapper
import de.honoka.sdk.spring.starter.core.SpringPropertiesHolder
import de.honoka.sdk.spring.starter.redis.basic.DefaultRedisTemplate
import de.honoka.sdk.util.kotlin.lang.copyFrom
import de.honoka.sdk.util.kotlin.lang.copyTo
import de.honoka.sdk.util.kotlin.text.toJsonObject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@Service
class UserService(
    private val springPropertiesHolder: SpringPropertiesHolder,
    private val redisTemplate: DefaultRedisTemplate,
    private val oAuth2Stub: OAuth2Stub
) : ServiceImpl<UserMapper, User>() {

    @Transactional
    fun register(params: UserRegisterRequest): UserRegisterResponse {
        val user = User().apply {
            username = params.username
            password = SecurityUtils.passwordEncoder.encode(params.password)
            authorities = "user"
            enabled = true
            locked = false
        }
        save(user)
        return user.copyTo(UserRegisterResponse())
    }

    fun login(params: UserLoginRequest): UserLoginResponse {
        val user = baseMapper.findByUsername(params.username!!)
        val invalid = user == null || !SecurityUtils.passwordEncoder.matches(
            params.password, user.password
        )
        if(invalid) error("用户名或密码错误")
        val loginId = UUID.randomUUID().toString()
        redisTemplate.opsForValue().set(
            "login_id:$loginId", 0, 10, TimeUnit.SECONDS
        )
        val callbackUrl = "http://localhost:${springPropertiesHolder.serverPort}/oauth2/callback"
        val callbackUrlEncoded = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8)
        val cliendId = springPropertiesHolder.applicationName
        val authCode = oAuth2Stub.authorize(
            "code", "microservice-demo-user", "all",
            callbackUrl, loginId
        ).data
        val tokenRes = oAuth2Stub.token(
            "grant_type=authorization_code&code=$authCode&redirect_uri=$callbackUrlEncoded",
            "Basic ${Base64.encode("$cliendId:$cliendId")}"
        ).toJsonObject()
        return UserLoginResponse().copyFrom(tokenRes)
    }
}
