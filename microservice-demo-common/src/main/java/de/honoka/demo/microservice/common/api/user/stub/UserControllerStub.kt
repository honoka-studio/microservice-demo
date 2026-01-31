package de.honoka.demo.microservice.common.api.user.stub

import de.honoka.demo.microservice.common.api.user.data.UserQueryRequest
import de.honoka.demo.microservice.common.api.user.entity.User
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient("microservice-demo-user", path = "/user")
interface UserControllerStub {

    @PostMapping("/query")
    fun query(@RequestBody params: UserQueryRequest): ApiResponse<List<User>>
}
