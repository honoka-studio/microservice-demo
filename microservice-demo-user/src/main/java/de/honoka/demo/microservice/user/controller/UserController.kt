package de.honoka.demo.microservice.user.controller

import de.honoka.demo.microservice.common.api.user.data.*
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.demo.microservice.user.service.UserService
import de.honoka.sdk.spring.starter.mybatis.queryBy
import de.honoka.sdk.spring.starter.various.toApiResponse
import de.honoka.sdk.util.kotlin.web.ApiResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/user")
@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody params: UserRegisterRequest): ApiResponse<UserRegisterResponse> =
        userService.register(params).toApiResponse()

    @GetMapping("/self")
    fun self(): ApiResponse<UserBasicInfo> = userService.self().toApiResponse()

    @PostMapping("/update")
    fun update(@RequestBody params: UserUpdateParams): ApiResponse<*> {
        userService.update(params)
        return ApiResponse.success()
    }
}

@RequestMapping("/internal/user")
@RestController
class InternalUserController(private val userService: UserService) {

    @PostMapping("/query")
    fun query(@RequestBody params: UserQueryRequest): List<User> =
        userService.baseMapper.queryBy(params, params.limit)

    @GetMapping("/queryById")
    fun queryById(@RequestParam id: Long): User? = userService.getById(id)
}
