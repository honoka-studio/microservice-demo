package de.honoka.demo.microservice.user.controller

import de.honoka.demo.microservice.common.api.user.data.*
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.demo.microservice.user.service.UserService
import de.honoka.sdk.spring.starter.mybatis.queryBy
import de.honoka.sdk.spring.starter.various.toApiResponse
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/user")
@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody params: UserRegisterRequest): ApiResponse<UserRegisterResponse> =
        userService.register(params).toApiResponse()

    @PostMapping("/query")
    fun query(@RequestBody params: UserQueryRequest): ApiResponse<List<User>> =
        userService.baseMapper.queryBy(params, params.limit).toApiResponse()

    @GetMapping("/queryById")
    fun queryById(@RequestParam id: Long): ApiResponse<User?> = userService.getById(id).toApiResponse()

    @GetMapping("/self")
    fun self(): ApiResponse<UserBasicInfo> = userService.self().toApiResponse()

    @PostMapping("/update")
    fun update(@RequestBody params: UserUpdateParams): ApiResponse<*> {
        userService.update(params)
        return ApiResponse.success()
    }
}
