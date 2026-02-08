package de.honoka.demo.microservice.auth.controller

import de.honoka.demo.microservice.auth.data.OAuth2CallbackResponse
import de.honoka.sdk.spring.starter.various.toApiResponse
import de.honoka.sdk.util.kotlin.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/oauth2")
@RestController
class OAuth2Controller {

    @GetMapping("/callback")
    fun callback(@RequestParam code: String): ApiResponse<OAuth2CallbackResponse> =
        OAuth2CallbackResponse(code).toApiResponse()
}
