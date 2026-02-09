package de.honoka.demo.microservice.common.api.auth.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId

data class WebRouteAuthority(

    @TableId(type = IdType.ASSIGN_ID)
    var id: Long? = null,

    var routeName: String? = null,

    var roles: String? = null
)
