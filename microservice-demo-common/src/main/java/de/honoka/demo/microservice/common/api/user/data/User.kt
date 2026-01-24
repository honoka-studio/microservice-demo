package de.honoka.demo.microservice.common.api.user.data

data class UserRegisterRequest(

    var username: String? = null,

    var password: String? = null
)

data class UserRegisterResponse(

    var id: Long? = null,

    var username: String? = null
)

data class UserLoginRequest(

    var username: String? = null,

    var password: String? = null
)

data class UserLoginResponse(

    var tokenType: String? = null,

    var accessToken: String? = null,

    var refreshToken: String? = null
)
