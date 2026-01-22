package de.honoka.demo.microservice.common.api.user.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import java.util.*

data class User(

    @TableId(type = IdType.ASSIGN_ID)
    var id: Long? = null,

    var username: String? = null,

    var password: String? = null,

    var authorities: String? = null,

    var enabled: Boolean? = null,

    var locked: Boolean? = null,

    var expireAt: Date? = null,

    var credentialsExpireAt: Date? = null
)
