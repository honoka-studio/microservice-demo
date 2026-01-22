package de.honoka.demo.microservice.user.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.sdk.spring.starter.mybatis.firstOrNull
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper : BaseMapper<User> {

    fun findByUsername(username: String): User? = firstOrNull {
        eq(User::username, username)
    }
}
