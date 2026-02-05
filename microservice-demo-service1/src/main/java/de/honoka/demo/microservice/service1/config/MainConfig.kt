package de.honoka.demo.microservice.service1.config

import de.honoka.demo.microservice.common.config.CommonBaseConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(CommonBaseConfig::class)
@Configuration
class MainConfig
