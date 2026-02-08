package de.honoka.demo.microservice.auth.controller

import de.honoka.demo.microservice.auth.service.AuthService
import de.honoka.demo.microservice.common.api.user.data.UserLoginRequest
import de.honoka.demo.microservice.common.api.user.data.UserLoginResponse
import de.honoka.sdk.spring.starter.various.toApiResponse
import de.honoka.sdk.util.kotlin.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody params: UserLoginRequest): ApiResponse<UserLoginResponse> =
        authService.login(params).toApiResponse()

    @GetMapping("/logout")
    fun logout(): ApiResponse<*> {
        authService.logout()
        return ApiResponse.success()
    }
}
