package de.honoka.demo.microservice.common.api.user.stub

import de.honoka.demo.microservice.common.api.user.data.UserQueryRequest
import de.honoka.demo.microservice.common.api.user.entity.User
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@FeignClient("microservice-demo-user", path = "/internal/user")
interface InternalUserControllerStub {

    @PostMapping("/query")
    fun query(@RequestBody params: UserQueryRequest): List<User>

    @GetMapping("/queryById")
    fun queryById(@RequestParam id: Long): User?
}
