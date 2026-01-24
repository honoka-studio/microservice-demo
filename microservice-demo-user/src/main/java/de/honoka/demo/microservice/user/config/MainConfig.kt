package de.honoka.demo.microservice.user.config

import de.honoka.demo.microservice.common.config.FeignBaseConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(FeignBaseConfig::class)
@Configuration
class MainConfig
