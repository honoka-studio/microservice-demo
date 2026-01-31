package de.honoka.demo.microservice.common.util

import cn.hutool.jwt.JWT
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object SecurityUtils {

    val passwordEncoder = BCryptPasswordEncoder()

    val currentUserId: Long
        get() {
            val requestAttributes = RequestContextHolder.currentRequestAttributes()
                as ServletRequestAttributes
            val token = requestAttributes.request.getHeader(HttpHeaders.AUTHORIZATION)
                .split(" ")[1]
            return JWT(token).payload.getClaim("sub").toString().toLong()
        }
}
