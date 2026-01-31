package de.honoka.demo.microservice.common.api.user.data

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
