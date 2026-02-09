package de.honoka.demo.microservice.auth.controller

import de.honoka.demo.microservice.auth.service.AuthService
import de.honoka.demo.microservice.common.api.auth.data.WebRouteAuthorityData
import de.honoka.sdk.spring.starter.various.toApiResponse
import de.honoka.sdk.util.kotlin.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auth")
@RestController
class AuthController(private val authService: AuthService) {

    @GetMapping("/web/routeAuthorities")
    fun webRouteAuthorities(): ApiResponse<Map<String, WebRouteAuthorityData>> =
        authService.webRouteAuthorities().toApiResponse()
}
