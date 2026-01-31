package de.honoka.demo.microservice.user.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import de.honoka.demo.microservice.common.api.user.data.UserBasicInfo
import de.honoka.demo.microservice.common.api.user.data.UserRegisterRequest
import de.honoka.demo.microservice.common.api.user.data.UserRegisterResponse
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.demo.microservice.user.mapper.UserMapper
import de.honoka.sdk.util.kotlin.lang.copyTo
import de.honoka.sdk.util.kotlin.lang.ignore
import de.honoka.sdk.util.kotlin.text.toJsonArray
import de.honoka.sdk.util.kotlin.text.toJsonString
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService : ServiceImpl<UserMapper, User>() {

    @Transactional
    fun register(params: UserRegisterRequest): UserRegisterResponse {
        val user = User().apply {
            username = params.username
            password = SecurityUtils.passwordEncoder.encode(params.password)
            avatar = "https://avatars.githubusercontent.com/u/44761321"
            authorities = listOf("*:*:*").toJsonString()
            enabled = true
            locked = false
        }
        save(user)
        return user.copyTo()
    }

    fun self(): UserBasicInfo {
        val user = getById(SecurityUtils.currentUserId)
        val userBasicInfo = user.copyTo(UserBasicInfo()) {
            ignore(UserBasicInfo::authorities)
        }
        userBasicInfo.authorities = user.authorities?.toJsonArray()
        return userBasicInfo
    }
}
