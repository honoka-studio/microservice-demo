package de.honoka.demo.microservice.common.api.user.data

import cn.hutool.json.JSONArray
import java.util.*

data class UserRegisterRequest(

    var username: String? = null,

    var password: String? = null
)

data class UserRegisterResponse(

    var id: Long? = null,

    var username: String? = null
)

data class UserQueryRequest(

    var id: Long? = null,

    var username: String? = null,

    var enabled: Boolean? = null,

    var locked: Boolean? = null,

    var limit: Long? = null
)

data class UserLoginRequest(

    var username: String? = null,

    var password: String? = null
)

data class UserLoginResponse(

    var loginId: String? = null
)

data class UserBasicInfo(

    var id: Long? = null,

    var username: String? = null,

    var avatar: String? = null,

    var authorities: JSONArray? = null,

    var enabled: Boolean? = null,

    var locked: Boolean? = null,

    var expireAt: Date? = null,

    var credentialsExpireAt: Date? = null
)
