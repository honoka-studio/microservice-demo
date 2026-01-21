package de.honoka.demo.microservice.auth.security

import de.honoka.sdk.spring.starter.core.springBean
import de.honoka.sdk.spring.starter.redis.basic.DefaultRedisTemplate
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 在存在自行保存的登录态的情况下，手动为[SecurityContextHolder.context]添加authentication信息
 */
object CustomLoginStatusFilter : OncePerRequestFilter() {

    private const val LOGIN_ID_HEADER = "X-Login-ID"

    private val redisTemplate by lazy { DefaultRedisTemplate::class.springBean }

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        request.getHeader(LOGIN_ID_HEADER)?.let {
            if(redisTemplate.hasKey("login_id:$it")) {
                /*
                 * 这里必须使用三个参数的UsernamePasswordAuthenticationToken构造方法，因为两个参数的构造方法会
                 * 将对象中的authenticated字段设为false，而三个参数的构造方法会设为true。
                 */
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    it, null, null
                )
            }
        }
        filterChain.doFilter(request, response)
    }
}
