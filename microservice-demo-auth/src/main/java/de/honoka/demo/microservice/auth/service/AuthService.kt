package de.honoka.demo.microservice.auth.service

import cn.hutool.core.lang.UUID
import de.honoka.demo.microservice.auth.mapper.WebRouteAuthorityMapper
import de.honoka.demo.microservice.common.api.auth.common.AuthConstants
import de.honoka.demo.microservice.common.api.auth.dao.AuthRedisDao
import de.honoka.demo.microservice.common.api.auth.data.WebRouteAuthorityData
import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.demo.microservice.common.api.user.data.UserQueryRequest
import de.honoka.demo.microservice.common.api.user.stub.InternalUserControllerStub
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.sdk.util.kotlin.bean.copyTo
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val internalUserControllerStub: InternalUserControllerStub,
    private val authRedisDao: AuthRedisDao,
    private val webRouteAuthorityMapper: WebRouteAuthorityMapper
) {

    fun login(params: UserLoginRequest): UserLoginResponse {
        val user = internalUserControllerStub.query(
            UserQueryRequest(username = params.username, limit = 1)
        ).firstOrNull()
        val invalid = user == null || !SecurityUtils.passwordEncoder.matches(
            params.password, user.password
        )
        if(invalid) error("用户名或密码错误")
        val loginId = UUID.randomUUID().toString()
        authRedisDao.setLoginId(loginId, user)
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
            AuthConstants.TOKEN_EXPIRE_TIME_MILLIS + 1000
        }
        if(timeout < 1) return
        authRedisDao.setLogoutId(jti, timeout)
    }

    fun webRouteAuthorities(): Map<String, WebRouteAuthorityData> {
        val list = webRouteAuthorityMapper.selectList(null).map {
            it.copyTo<WebRouteAuthorityData>()
        }
        return list.associateBy { it.routeName!! }
    }
}
