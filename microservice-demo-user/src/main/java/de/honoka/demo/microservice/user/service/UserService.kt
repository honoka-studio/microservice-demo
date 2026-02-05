package de.honoka.demo.microservice.user.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import de.honoka.demo.microservice.common.api.user.data.UserBasicInfo
import de.honoka.demo.microservice.common.api.user.data.UserRegisterRequest
import de.honoka.demo.microservice.common.api.user.data.UserRegisterResponse
import de.honoka.demo.microservice.common.api.user.data.UserUpdateParams
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.demo.microservice.common.util.SecurityUtils
import de.honoka.demo.microservice.user.mapper.UserMapper
import de.honoka.sdk.spring.starter.mybatis.first
import de.honoka.sdk.util.kotlin.bean.copyTo
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
            authorities = params.authorities?.toString()
            enabled = true
            locked = false
        }
        save(user)
        return user.copyTo()
    }

    fun self(): UserBasicInfo {
        val user = getById(SecurityUtils.currentUserId)
        val userBasicInfo = user.copyTo<UserBasicInfo>()
        return userBasicInfo
    }

    @Transactional
    fun update(params: UserUpdateParams) {
        val user = baseMapper.first {
            select(User::id)
            if(params.id != null) {
                eq(User::id, params.id)
            } else {
                eq(User::username, params.username)
            }
        }
        val realParams = params.copyTo<User> {
            ignore(User::username)
            target.id = user.id
        }
        updateById(realParams)
    }
}
