package de.honoka.demo.microservice.service1.controller

import de.honoka.sdk.spring.starter.various.toApiResponse
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {

    @GetMapping("/hello")
    fun hello(): ApiResponse<*> = "Hello from service1.".toApiResponse()

    @GetMapping("/hello2")
    fun hello2(): ApiResponse<*> = "Hello2 from service1.".toApiResponse()

    @GetMapping("/hello3")
    fun hello3(): ApiResponse<*> = "Hello3 from service1.".toApiResponse()
}
