package de.honoka.demo.microservice.auth.security

import de.honoka.demo.microservice.common.api.auth.dao.AuthRedisDao
import de.honoka.sdk.spring.starter.core.springBeanLazy
import de.honoka.sdk.spring.starter.security.springAuthorityObjects
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 在存在自行保存的登录态的情况下，手动为[SecurityContextHolder.context]添加authentication信息
 */
object LoginStatusFilter : OncePerRequestFilter() {

    private const val LOGIN_ID_HEADER = "X-Login-ID"

    private val authRedisDao by AuthRedisDao::class.springBeanLazy

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val user = request.getHeader(LOGIN_ID_HEADER)?.let {
            authRedisDao.findUserByLoginId(it)
        }
        user?.let {
            SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken
                .authenticated(it.id!!, null, it.springAuthorityObjects)
        }
        filterChain.doFilter(request, response)
    }
}
