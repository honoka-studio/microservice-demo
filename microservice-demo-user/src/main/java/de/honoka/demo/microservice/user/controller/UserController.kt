package de.honoka.demo.microservice.user.controller

import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.demo.microservice.common.api.user.data.UserRegisterRequest
import de.honoka.demo.microservice.common.api.user.data.UserRegisterResponse
import de.honoka.demo.microservice.user.service.UserService
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody params: UserRegisterRequest): ApiResponse<UserRegisterResponse> =
        ApiResponse.success(userService.register(params))

    @PostMapping("/login")
    fun login(@RequestBody params: UserLoginRequest): ApiResponse<UserLoginResponse> =
        ApiResponse.success(userService.login(params))
}
