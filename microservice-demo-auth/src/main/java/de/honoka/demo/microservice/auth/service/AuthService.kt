package de.honoka.demo.microservice.auth.service

import cn.hutool.core.lang.UUID
import de.honoka.demo.microservice.common.api.auth.dao.AuthRedisDao
import de.honoka.demo.microservice.common.api.user.data.UserBasicInfo
import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.demo.microservice.common.api.user.data.UserQueryRequest
import de.honoka.demo.microservice.common.api.user.stub.UserControllerStub
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.sdk.util.kotlin.bean.copyTo
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userControllerStub: UserControllerStub,
    private val authRedisDao: AuthRedisDao
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
        val userBasicInfo = user.copyTo<UserBasicInfo>()
        authRedisDao.setLoginId(loginId, userBasicInfo)
        return UserLoginResponse(loginId)
    }

    fun logout() {
        val jwtPayloads = SecurityUtils.currentJwt?.payloads ?: return
        val jti = jwtPayloads.getStr("jti").apply {
            if(isNullOrBlank()) return
        }
        val exp = jwtPayloads.getLong("exp")
        val currentTime = System.currentTimeMillis()
        val timeout = if(exp != null) {
            exp * 1000 - currentTime + 1000
        } else {
            301 * 1000L
        }
        if(timeout < 1) return
        authRedisDao.setLogoutId(jti, timeout)
    }
}
