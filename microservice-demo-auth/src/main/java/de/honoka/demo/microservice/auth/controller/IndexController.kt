package de.honoka.demo.microservice.auth.controller

import de.honoka.demo.microservice.auth.service.AuthService
import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody params: UserLoginRequest): ApiResponse<UserLoginResponse> =
        ApiResponse.success(authService.login(params))
}
