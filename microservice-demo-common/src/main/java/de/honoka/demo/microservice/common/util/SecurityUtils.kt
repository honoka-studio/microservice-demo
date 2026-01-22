package de.honoka.demo.microservice.common.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object SecurityUtils {

    val passwordEncoder =  BCryptPasswordEncoder()
}
