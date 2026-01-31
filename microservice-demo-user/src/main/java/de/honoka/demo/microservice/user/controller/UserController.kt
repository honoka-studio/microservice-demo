package de.honoka.demo.microservice.user.controller

import de.honoka.demo.microservice.common.api.user.data.UserQueryRequest
import de.honoka.demo.microservice.common.api.user.data.UserRegisterRequest
import de.honoka.demo.microservice.common.api.user.data.UserRegisterResponse
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.demo.microservice.user.service.UserService
import de.honoka.sdk.spring.starter.mybatis.queryBy
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/user")
@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody params: UserRegisterRequest): ApiResponse<UserRegisterResponse> =
        ApiResponse.success(userService.register(params))

    @PostMapping("/query")
    fun query(@RequestBody params: UserQueryRequest): ApiResponse<List<User>> =
        ApiResponse.success(userService.baseMapper.queryBy(params, params.limit))
}
