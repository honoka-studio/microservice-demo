package de.honoka.demo.microservice.common.api.auth.stub

import de.honoka.sdk.util.web.ApiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@FeignClient("microservice-demo-auth", path = "/oauth2")
interface OAuth2Stub {

    @GetMapping("/authorize")
    fun authorize(
        @RequestParam("response_type") responseType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("scope") scope: String,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestHeader("X-Login-ID") loginId: String
    ): ApiResponse<String>

    @PostMapping("/token", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun token(
        @RequestBody params: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String
    ): String
}
