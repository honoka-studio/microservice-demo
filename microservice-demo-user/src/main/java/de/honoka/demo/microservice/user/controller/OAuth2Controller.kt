package de.honoka.demo.microservice.user.controller

import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/oauth2")
@RestController
class OAuth2Controller {

    @GetMapping("/callback")
    fun getCallback(@RequestParam code: String): ApiResponse<String> = ApiResponse.success(code)
}
