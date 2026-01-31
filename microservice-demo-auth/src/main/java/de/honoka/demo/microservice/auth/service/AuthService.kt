package de.honoka.demo.microservice.auth.service

import cn.hutool.core.lang.UUID
import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.demo.microservice.common.api.user.data.UserQueryRequest
import de.honoka.demo.microservice.common.api.user.stub.UserControllerStub
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.sdk.spring.starter.redis.DefaultRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userControllerStub: UserControllerStub,
    private val redisTemplate: DefaultRedisTemplate
) {

    fun login(params: UserLoginRequest): UserLoginResponse {
        val user = userControllerStub.query(
            UserQueryRequest(username = params.username, limit = 1)
        ).data.firstOrNull()
        val invalid = user == null || !SecurityUtils.passwordEncoder.matches(
            params.password, user.password
        )
        if(invalid) error("用户名或密码错误")
        val loginId = UUID.randomUUID().toString()
        redisTemplate.opsForValue().set(
            "login_id:$loginId", 0, 10, TimeUnit.SECONDS
        )
        return UserLoginResponse(loginId)
    }
}
