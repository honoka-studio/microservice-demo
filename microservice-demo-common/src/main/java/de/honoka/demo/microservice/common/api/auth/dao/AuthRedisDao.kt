package de.honoka.demo.microservice.common.api.auth.dao

import de.honoka.demo.microservice.common.api.auth.common.AuthConstants
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.sdk.spring.starter.redis.DefaultRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class AuthRedisDao(private val redisTemplate: DefaultRedisTemplate) {

    fun setLoginId(loginId: String, user: User) {
        redisTemplate.opsForValue().set(
            "login_id:$loginId", user, 10, TimeUnit.SECONDS
        )
    }

    fun findUserByLoginId(loginId: String): User? =
        redisTemplate.opsForValue()["login_id:$loginId"] as User?

    fun setLogoutId(logoutId: String, timeout: Long) {
        redisTemplate.opsForValue().set(
            "logout_id:$logoutId", 0, timeout, TimeUnit.MILLISECONDS
        )
    }

    fun hasLogoutId(logoutId: String): Boolean = redisTemplate.hasKey("logout_id:$logoutId")!!

    fun setRevokedTokenTime(userId: Long) {
        redisTemplate.opsForValue().set(
            "revoked_token_time:$userId", System.currentTimeMillis(),
            AuthConstants.TOKEN_EXPIRE_TIME_MILLIS + 1000, TimeUnit.MILLISECONDS
        )
    }

    fun getRevokedTokenTime(userId: Long): Long? =
        redisTemplate.opsForValue()["revoked_token_time:$userId"] as Long?
}
