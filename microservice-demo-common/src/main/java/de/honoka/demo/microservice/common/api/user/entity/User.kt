package de.honoka.demo.microservice.common.api.user.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import de.honoka.sdk.spring.starter.security.DefaultUser

data class User(

    var avatar: String? = null
) : DefaultUser() {

    @TableId(type = IdType.ASSIGN_ID)
    override var id: Long? = null
}
