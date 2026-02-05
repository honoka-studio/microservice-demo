package de.honoka.demo.microservice.common.api.auth.dao

import de.honoka.demo.microservice.common.api.user.data.UserBasicInfo
import de.honoka.sdk.spring.starter.redis.DefaultRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class AuthRedisDao(private val redisTemplate: DefaultRedisTemplate) {

    fun setLoginId(loginId: String, user: UserBasicInfo) {
        redisTemplate.opsForValue().set(
            "login_id:$loginId", user, 10, TimeUnit.SECONDS
        )
    }

    fun findUserByLoginId(loginId: String): UserBasicInfo? =
        redisTemplate.opsForValue()["login_id:$loginId"] as UserBasicInfo?

    fun setLogoutId(logoutId: String, timeout: Long) {
        redisTemplate.opsForValue().set(
            "logout_id:$logoutId", 0, timeout, TimeUnit.MILLISECONDS
        )
    }

    fun hasLogoutId(logoutId: String): Boolean = redisTemplate.hasKey("logout_id:$logoutId")!!
}
