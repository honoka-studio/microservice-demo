package de.honoka.demo.microservice.service1.controller

import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AllController {

    @GetMapping("/hello")
    fun hello(): ApiResponse<*> = ApiResponse.success("Hello from service1.")
}
